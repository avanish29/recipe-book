import { ChangeDetectionStrategy, Component, HostListener, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'confirm-dialog',
    templateUrl: './confirm-dialog.component.html',
    styles: [`
        .header, .dialog-message {
            text-transform: lowercase;
        }
        .header::first-letter, .dialog-message::first-letter {
            text-transform: uppercase;
        }
        .btn-cancel {
            background-color: red;
            color: #fff;
        }`
    ]
})
export class ConfirmDialogComponent {
    constructor(@Inject(MAT_DIALOG_DATA) public data: {
        cancelText: string,
        confirmText: string,
        message: string,
        title: string
    }, private mdDialogRef: MatDialogRef<ConfirmDialogComponent>) { }

    public cancel() {
        this.close(false);
    }
    public close(value: boolean) {
        this.mdDialogRef.close(value);
    }
    public confirm() {
        this.close(true);
    }
    @HostListener("keydown.esc")
    public onEsc() {
        this.close(false);
    }
}