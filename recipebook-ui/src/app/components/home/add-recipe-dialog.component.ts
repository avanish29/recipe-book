import { HttpErrorResponse } from '@angular/common/http';
import {Component, Inject, OnInit, Optional} from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { RecipeService } from 'src/app/services/recipe.service';
import { RecipeModel } from './recipe.mode';

@Component({
    selector: 'add-recipe-dialog',
    templateUrl: './add-recipe-dialog.component.html'
})
export class AddRecipeDialog implements OnInit {
    recipeGuid: string = "";
    addRecipeFormGroup: FormGroup;
    model = {} as RecipeModel;
    errorMsg: string = "";
    loading: boolean = false;

    constructor(private formBuilder: FormBuilder, private remoteSrv: RecipeService, private dialog: MatDialog, private dialogRef: MatDialogRef<AddRecipeDialog>, @Optional() @Inject(MAT_DIALOG_DATA) public data: any){
        this.model.ingredients = [];
        this.model.ingredients.push('');
        this.buildForm();
        this.recipeGuid = data.selectedRecipe;
    }


    ngOnInit() {
        if(this.recipeGuid) {
            this.remoteSrv.getRecipe(this.recipeGuid).subscribe({
                next: (data) => {
                    this.loading = false;
                    this.model = data;
                    this.buildForm();
                    for(var ingredient of this.model.ingredients) {
                        this.addIngredient(ingredient);
                    }
                },
                error: (errorResp: HttpErrorResponse) => {
                    console.log(errorResp);
                    this.errorMsg = errorResp.error.message;
                    this.loading = false;
                }
            })
        }
    }

    buildForm() {
        this.addRecipeFormGroup = this.formBuilder.group({
            name: [this.model.name, [Validators.required, Validators.maxLength(255), Validators.minLength(5)]],
            suitableFor: [this.model.suitableFor, Validators.required],
            vegetarian: [this.model.vegetarian, Validators.required],
            ingredients: this.formBuilder.array([]),
            cookingInstruction: [this.model.cookingInstruction, [Validators.required]],
        });
    }

    closeAlert() {
        this.errorMsg = "";
    }

    addIngredient(value: string): void {
        this.ingredientsFormArray.push(
            this.newIngredient(value)
        );
    }
    
    removeIngredient(index: number) {
        if(this.model.ingredients.length > 1) {
            this.ingredientsFormArray.removeAt(index);
        }
    }

    get ingredientsFormArray() {
        return (<FormArray> this.addRecipeFormGroup.get('ingredients'));
    }

    newIngredient(value: string): FormGroup {
        return this.formBuilder.group({
            ingredient: [value, Validators.required]
        })
    }

    onAddRecipe() : void {
        this.loading = true;
        this.model = Object.assign(this.model, this.addRecipeFormGroup.value);
        this.model.ingredients = [];
        for(var name of this.addRecipeFormGroup.controls['ingredients'].value) {
            this.model.ingredients.push(name.ingredient); 
        }
        console.log(this.model);
        if(this.recipeGuid) {
            this.remoteSrv.updateRecipe(this.recipeGuid, this.model).subscribe({
                next: (data) => {
                    this.loading = false;
                    this.model = data;
                    this.dialogRef.close('SUCCESS');
                },
                error: (errorResp: HttpErrorResponse) => {
                    console.log(errorResp);
                    this.errorMsg = errorResp.error.message;
                    this.loading = false;
                }
            });
        } else {
            this.remoteSrv.createRecipe(this.model).subscribe({
                next: (data) => {
                    this.loading = false;
                    this.model = data;
                    this.dialogRef.close('SUCCESS');
                },
                error: (errorResp: HttpErrorResponse) => {
                    console.log(errorResp);
                    this.errorMsg = errorResp.error.message;
                    this.loading = false;
                }
            });
        }
    }
}