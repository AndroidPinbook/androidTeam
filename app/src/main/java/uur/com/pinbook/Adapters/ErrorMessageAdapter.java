package uur.com.pinbook.Adapters;

/**
 * Created by mac on 4.12.2017.
 */

public enum ErrorMessageAdapter{

    //  Add Error Tags here -with next number
    EMAIL_EMPTY(101),
    PASSWORD_EMPTY(102),
    INVALID_USER(103),
    INVALID_CREDENTIALS(104),
    COLLISION_EXCEPTION(105),
    FAIL_TO_SEND_VERIFICATION_MAIL(106),
    UNKNOW_ERROR(107);

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
                message = "Email boş bırakılamaz";
                break;
            case 102:
                message = "Şifre boş bırakılamaz";
                break;
            case 103:
                message = "User is invalid";
                break;
            case 104:
                message = "Yanlış e-posta veya şifre!";
                break;
            case 105:
                message = "E-mail adresi kayıtlı. Lütfen başka e-mail adresi giriniz.";
                break;
            case 106:
                message = "Email aktivasyon linki gönderimi başarısız, lütfen tekrar deneyiniz.";
                break;
            case 107:
                message = "Firebase HATA!";
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