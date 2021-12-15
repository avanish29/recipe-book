export interface RecipeModel {
    uuid: string;
    createdAt: string;
    name: string;
    suitableFor: number;
    ingredients: string[];
    cookingInstruction: string;
    vegetarian: boolean;
}