import { inject , OnInit, Component } from "@angular/core";
import { OrganisationService } from "../../../../core/services/organisation.service";
import { OrganisationResponse } from "../../models/organisation-model";

@Component({
    selector:'app-get-organisation',
    standalone:true,
    templateUrl: './get-organisation.html'
})

export class GetOrganisation implements OnInit{
    private readonly organisationService = inject(OrganisationService)

    organisations: OrganisationResponse[] = [];

    isLoading = true;
    errorMessage = '';

    ngOnInit(): void {
        this.loadOrganisation();
    }
    loadOrganisation(): void {
        this.isLoading =  true;
        this.errorMessage = '';

        this.organisationService.getOrganisation()
        .subscribe({
            next: (response) =>{
                this.organisations = response;
                this.isLoading = true;
            },
            error: (error) =>{
                this.errorMessage = 
                error.error?.message || 'Failed to Organisations';
                this.isLoading = false;
            }
        })


    }
}