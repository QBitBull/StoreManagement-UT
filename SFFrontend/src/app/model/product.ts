export class Product{

    id: number;
    productName: string;
    boughtPrice: string;
    soldPrice: string;
    nameBrand: string;
    nameCategory: string;
    quantity: string;

    constructor(){
        this.productName = '';
        this.boughtPrice = '';
        this.soldPrice = '';
        this.nameBrand = '';
        this.nameCategory = '';
        this.quantity = '';
    }
}