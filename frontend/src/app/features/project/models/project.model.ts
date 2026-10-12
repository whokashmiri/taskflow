export interface ProjectRequest{
    name:string;
    organisationId:string;
}

export interface ProjectResponse{
    id:string;
    name:string;
    organisationId:string;
    createdBy: string;
    createdAt: string;
    updatedAt: string;
}