import { SelectionModel } from "@angular/cdk/collections";
import { AfterViewInit, ChangeDetectorRef, Component, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { RecipeModel } from "./recipe.mode";
import { merge, of as observableOf } from 'rxjs';
import { catchError, map, startWith, switchMap } from 'rxjs/operators';
import { RecipeService } from "src/app/services/recipe.service";
import { MatDialog } from "@angular/material/dialog";
import { ConfirmDialogService } from "../dialog/confirm-dialog.service";
import { HttpErrorResponse } from "@angular/common/http";
import { MatSort } from "@angular/material/sort";
import { AddRecipeDialog } from "./add-recipe-dialog.component";

@Component({
    selector: 'recipe-list',
    templateUrl: './list-recipe.component.html',
    styleUrls: ['./list-recipe.component.css']
})
export class RecipeListComponent implements AfterViewInit {
    displayedColumns: string[] = ['createdAt', 'name', 'suitableFor', 'vegetarian', 'action'];
    data: RecipeModel[] = [];
    selection = new SelectionModel<RecipeModel>(true, []);

    resultsLength = 0;
    isLoadingResults = true;

    @ViewChild(MatPaginator) paginator: MatPaginator;

    constructor(private remoteSrv: RecipeService, private cdr: ChangeDetectorRef, private dialog: MatDialog, private dialogService: ConfirmDialogService) { 

    }

    ngAfterViewInit() {
      this.loadRecipes();
    }

    loadRecipes(): void {
        merge(this.paginator.page).pipe(
            startWith({}),
            switchMap(
                () => {
                    this.isLoadingResults = true;
                    return this.remoteSrv.getAllRecipies(this.paginator.pageIndex, this.paginator.pageSize);
                }
            ),
            map(
                response => {
                    this.isLoadingResults = false;
                    this.resultsLength = response.totalItems;
                    return response.contents;
                }
            ),
            catchError(
                () => {
                    this.isLoadingResults = false;
                    return observableOf([]);
                }
            )
        ).subscribe(response => this.data = response);
    }

    onDeleteAction(element: RecipeModel) {
        const options = {
            title: 'Delete?',
            message: 'Are you sure you want to delete the selected items ?',
            cancelText: 'CANCEL',
            confirmText: 'YES, DELETE'
        };

        this.dialogService.open(options);

        this.dialogService.confirmed().subscribe(confirmed => {
            if (confirmed) {
                this.isLoadingResults = true;
                this.remoteSrv.deleteRecipe(element.uuid).subscribe({
                    next: (data) =>  {
                        this.isLoadingResults = false;
                        this.selection.clear();
                        this.loadRecipes();
                    },
                    error: (errorResp: HttpErrorResponse) => {
                        console.log(errorResp);
                        this.isLoadingResults = false;
                    }
                });
            }
        });
    }

    openAddDialog() {
      const dialogRef = this.dialog.open(AddRecipeDialog, { width: '1300px', data: { selectedRecipe: null }});
  
      dialogRef.afterClosed().subscribe(result => {
        console.log(`Dialog result: ${result}`);
        if(result === 'SUCCESS') {
          this.loadRecipes();
        }
      });
  }

  openEditDialog(recipe: RecipeModel) {
    const dialogRef = this.dialog.open(AddRecipeDialog, { width: '1300px', data: { selectedRecipe: recipe.uuid }});

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
      if(result === 'SUCCESS') {
        this.loadRecipes();
      }
    });
}
}