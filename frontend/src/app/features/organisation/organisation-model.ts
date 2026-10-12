export interface OrganisationRequest {
    name:string;
}
export interface OrganisationResponse {
    id: string;
    name: string;
    ownerId: string;
    createdAt: string;
    updatedAt:string;
}