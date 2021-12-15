import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ConfirmDialogComponent } from './confirm-dialog.component';
import { map, take } from 'rxjs/operators';

@Injectable()
export class ConfirmDialogService {
    dialogRef!: MatDialogRef<ConfirmDialogComponent>;

    constructor(private dialog: MatDialog) { }

    public open(options: { title: any; message: any; cancelText: any; confirmText: any; }) {
        this.dialogRef = this.dialog.open(ConfirmDialogComponent, {
            data: {
                title: options.title,
                message: options.message,
                cancelText: options.cancelText,
                confirmText: options.confirmText
            }
        });
    }

    public confirmed(): Observable<any> {
        return this.dialogRef.afterClosed().pipe(take(1), map(res => {
            return res;
        }));
    }
}