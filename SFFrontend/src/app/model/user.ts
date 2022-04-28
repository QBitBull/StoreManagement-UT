export class User{
    public id: number;
    public userID: string ;
    public firstName: string;
    public lastName: string;
    public username: string;
    public password: string;
    public email: string;
    public profileImageURL: string;

    public lastLoginDate: Date;
    public lastLoginDateDisplay: Date;
    public joinDate: Date;

    public role: string;
    public authorities: string[];

    public active: boolean;
    public notLocked: boolean;

    constructor() {
        this.firstName = '';
        this.lastName = '';
        this.username = '';
        this.email = '';
        this.authorities = [];
        this.active = false;
        this.notLocked = false;
        this.role = '';
    }
}