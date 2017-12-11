package uur.com.pinbook.Controller;

/**
 * Created by mac on 4.12.2017.
 */

public enum ErrorMessageAdapter{

    //  Add Error Tags here -with next number
    EMAIL_EMPTY(101),
    PASSWORD_EMPTY(102),
    INVALID_USER(103),
    INVALID_CREDENTIALS(104),
    COLLISION_EXCEPTION(105);

    public final int number;

    private ErrorMessageAdapter(int number) {
        this.number = number;
    }

    public int getNumber(){
        return number;
    }

    // Add Error Code explanation here..
    public String getText() {

        String message;

        switch(number) {
            case 101:
                message = "Email can't be empty";
                break;
            case 102:
                message = "Password can't be empty";
                break;
            case 103:
                message = "User is invalid";
                break;
            case 104:
                message = "Yanlış e-posta veya şifre!";
                break;
            case 105:
                message = "E-mail adresi kayıtlı. Lütfen başka e-mail adresi giriniz..";
                break;
            default:
                message = "Unknown Error";


        }

        return message;
    }

    public static void setError(int number){
        return;
    }


}