package au.mccann.oztaxreturn.common;

public class Constants {

    //http code
    public static final int HTTP_CODE_OK = 200;
    public static final int HTTP_CODE_CREATED = 201;
    public static final int HTTP_CODE_NO_CONTENT = 204;
    public static final int HTTP_CODE_BAD_REQUEST = 400;
    public static final int HTTP_CODE_UNAUTHORIZED = 401;
    public static final int HTTP_CODE_FORBIDEN = 403;
    public static final int HTTP_CODE_NOT_FOUND = 404;
    public static final int HTTP_CODE_UNPROCESSABLE_ENTITY = 422;
    public static final int HTTP_CODE_BLOCK_USER = 423;


    public static final String SYSTEM_ERROR = "system.error";

    public static final String PARAMETER_USERNAME = "username";
    public static final String PARAMETER_MOBILE = "phone";
    public static final String PARAMETER_PASSWORD = "password";
    public static final String PARAMETER_RE_PASSWORD = "confirm_password";
    public static final String USERNAME_REQUIRE = "username.required";
    public static final String USERNAME_UNIQUE = "username.unique";
    public static final String USERNAME_MAX = "username.max";
    public static final String PASSWORD_REQUIRE = "password.required";
    public static final String PASSWORD_CONFIRM = "password.confirm";
    public static final String RE_PASSWORD_REQUIRE = "confirm_password.required";
    public static final String EMAIL_REQUIRE = "email.required";
    public static final String EMAIL_EMAIL = "email.email";
    public static final String PHONE_REQUIRED = "phone.required";
    public static final String PHONE_UNIQUE = "phone.unique";
    public static final String APP_LIST_EXTRA = "app_list_extra";
    public static final String PARAMETER_INCOME_TFN = "tfn_number";
    public static final String PARAMETER_INCOME_FIRST_NAME = "first_name";
    public static final String PARAMETER_INCOME_MID_NAME = "middle_name";
    public static final String PARAMETER_INCOME_LAST_NAME = "last_name";
    public static final String PARAMETER_INCOME_BIRTH_DAY = "birthday";
    public static final String PARAMETER_INCOME_CONTENT = "income_content";
    public static final String PARAMETER_INCOME_CONTENT_ATTACHMENTS = "income_content_attachments";
    public static final String PARAMETER_DEDUCTION_CONTENT = "eduction_content";
    public static final String PARAMETER_DEDUCTION_ATTACHMENTS = "deduction_content_attachments";
    public static final String PARAMETER_APP_ID = "application_id";
    public static final String PARAMETER_APP_TITLE = "application_title";
    public static final String PARAMETER_APP_FIRST_NAME = "application_first_name";
    public static final String PARAMETER_APP_MID_NAME = "application_middle_name";
    public static final String PARAMETER_APP_LAST_NAME = "application_last_name";
    public static final String PARAMETER_APP_BIRTH_DAY = "application_birthday";
    public static final String PARAMETER_APP_GENDER = "application_gender";
    public static final String PARAMETER_APP_LOCAL = "application_local";
    public static final String PARAMETER_APP_BANK_ACCOUNT_NAME = "application_bank_account_name";
    public static final String PARAMETER_APP_BANK_ACCOUNT_NUMBER = "application_bank_account_number";
    public static final String PARAMETER_APP_BANK_ACCOUNT_BSB = "application_bank_account_bsb";
    public static final String PARAMETER_APP_STREET = "application_street";
    public static final String PARAMETER_APP_SUBURB = "application_suburb";
    public static final String PARAMETER_APP_STATE = "application_state";
    public static final String PARAMETER_APP_POST_CODE = "application_postcode";
    public static final String PARAMETER_APP_PHONE = "application_phone";
    public static final String PARAMETER_APP_EMAIL = "application_email";
    public static final String PARAMETER_WAGE_ATTACHMENTS = "income_wage_attachments";
    public static final String KEY_BASIC_INFORMATION = "key_basic_information";
    public static final String PARAMETER_BASIC_INCOME_OTHER = "income_others";
    public static final String PARAMETER_BASIC_CONTENT = "content";
    public static final String PARAMETER_ATTACHMENTS = "attachments";
    public static final String PARAMETER_BASIC_INCOME_DEDUCTION = "deduction";
    public static final String PARAMETER_BASIC_INFO = "personal_information";
    public static final String PARAMETER_BASIC_INFO_TITLE = "title";
    public static final String PARAMETER_BASIC_INFO_FIRST_NAME = "first_name";
    public static final String PARAMETER_BASIC_INFO_MIDDLE_NAME = "middle_name";
    public static final String PARAMETER_BASIC_INFO_LAST_NAME = "last_name";
    public static final String PARAMETER_BASIC_INFO_BIRTHDAY = "birthday";
    public static final String PARAMETER_BASIC_INFO_GENDER = "gender";
    public static final String PARAMETER_BASIC_INFO_LOCAL = "local";
    public static final String PARAMETER_BASIC_INFO_BANK_NAME = "bank_account_name";
    public static final String PARAMETER_BASIC_INFO_BANK_NUMBER = "bank_account_number";
    public static final String PARAMETER_BASIC_INFO_BANK_BSB = "bank_account_bsb";
    public static final String PARAMETER_BASIC_INFO_STREET = "street";
    public static final String PARAMETER_BASIC_INFO_SUBURB = "suburb";
    public static final String PARAMETER_BASIC_INFO_POST_CODE = "postcode";
    public static final String PARAMETER_BASIC_INFO_STATE = "state";
    public static final String PARAMETER_BASIC_INFO_PHONE = "phone";
    public static final String PARAMETER_BASIC_INFO_EMAIL = "email";
    public static final String PARAMETER_REVIEW_INCOM_JOB_ID = "id";
    public static final String PARAMETER_REVIEW_INCOM_JOB_GROSS = "gross_income";
    public static final String PARAMETER_REVIEW_INCOM_JOB_TAX = "tax_withheld";
    public static final String PARAMETER_REVIEW_INCOM_JOB_ALLOWANCES = "allowances";
    public static final String PARAMETER_REVIEW_INCOM_JOB_FRINGE = "fringe_benefits";
    public static final String PARAMETER_REVIEW_INCOM_JOB_EMPLOYER = "employer_con";
    public static final String PARAMETER_REVIEW_INCOM_JOB_COMPANY_NAME = "company_name";
    public static final String PARAMETER_REVIEW_INCOM_JOB_COMPANY_ABN = "company_abn";
    public static final String PARAMETER_REVIEW_INCOM_JOB_COMPANY_CONTACT = "company_contact";
    public static final String PARAMETER_REVIEW_INCOME_JOB = "jobs";
    public static final String PARAMETER_REVIEW_INCOME_GOVEMENT = "gov_payments";
    public static final String PARAMETER_REVIEW_HAD = "had";
    public static final String PARAMETER_REVIEW_INCOME_GOVEMENT_TYPE = "income_type";
    public static final String PARAMETER_REVIEW_INCOME_GOVEMENT_GROSS = "gross_income";
    public static final String PARAMETER_REVIEW_INCOME_GOVEMENT_TAX = "tax_withheld";
    public static final String PARAMETER_REVIEW_INCOME_BANK_NAME = "bank_name";
    public static final String PARAMETER_REVIEW_INCOME_BANK_NUMBER = "account_number";
    public static final String PARAMETER_REVIEW_INCOME_BANK_TOTAL_INTEREST = "total_interest";
    public static final String PARAMETER_REVIEW_INCOME_BANK_TAX = "tax_withheld";
    public static final String PARAMETER_REVIEW_INCOME_BANK_FEES = "fees";
    public static final String PARAMETER_REVIEW_INCOME_BANK = "bank_interests";
    public static final String KEY_REVIEW = "key_review";
    public static final String PERSONNAL_INFO_EXTRA = "personnal_info_extra";
    public static final String PARAMETER_FEE_EXTRA = "fee_extra";
    public static final String URL_EXTRA = "url_extra";
    public static final String TITLE_INFO_EXTRA = "title_extra";
    public static final String PARAMETER_PUT_ID = "id";
    public static final String PARAMETER_REVIEW_INCOM_DIVIDEND_COMPANY = "company_name";
    public static final String PARAMETER_REVIEW_INCOM_DIVIDEND_UNFRANKED = "unfranked";
    public static final String PARAMETER_REVIEW_INCOM_DIVIDEND_FRANKED = "franked";
    public static final String PARAMETER_REVIEW_INCOM_DIVIDEND_FRANKED_CREATE = "franking_credits";
    public static final String PARAMETER_REVIEW_INCOM_DIVIDEND_TAX = "tax_withheld";
    public static final String PARAMETER_REVIEW_INCOME_ETPS = "etps";
    public static final String PARAMETER_REVIEW_ETPS_PAYMENT_DATE = "payment_date";
    public static final String PARAMETER_REVIEW_ETPS_PAYMENT_ABN = "payer_abn";
    public static final String PARAMETER_REVIEW_ETPS_PAYMENT_TAX = "tax_withheld";
    public static final String PARAMETER_REVIEW_ETPS_PAYMENT_COM = "taxable_com";
    public static final String PARAMETER_REVIEW_ETPS_PAYMENT_CODE = "code";
    public static final String PARAMETER_REVIEW_INCOME_RENTAL = "rentals";
    public static final String PARAMETER_REVIEW_INCOME_RENTAL_OWNERSHIP_PER = "ownership_per";
    public static final String PARAMETER_REVIEW_INCOME_RENTAL_STREET = "street";
    public static final String PARAMETER_REVIEW_INCOME_RENTAL_SUBURB = "suburb";
    public static final String PARAMETER_REVIEW_INCOME_RENTAL_STARTE = "state";
    public static final String PARAMETER_REVIEW_INCOME_RENTAL_POST_CODE = "postcode";
    public static final String PARAMETER_REVIEW_INCOME_RENTAL_FIRST_DATE = "first_earned";
    public static final String PARAMETER_REVIEW_INCOME_RENTAL_INCOME = "income";
    public static final String PARAMETER_REVIEW_INCOME_RENTAL_EXPENSES = "expenses";
    public static final String PARAMETER_REVIEW_INCOME_DIVIDEND = "dividends";
    public static final String PARAMETER_REVIEW_INCOME_ANNUITY = "annuities";
    public static final String PARAMETER_REVIEW_INCOM_ANNUITY_TAX = "tax_withheld";
    public static final String PARAMETER_REVIEW_INCOM_ANNUITY_COM_TAX = "taxable_com_taxed";
    public static final String PARAMETER_REVIEW_INCOM_ANNUITY_COM_UNTAX = "taxable_com_untaxed";
    public static final String PARAMETER_REVIEW_INCOM_ANNUITY_ARREAR_TAX = "arrears_taxed";
    public static final String PARAMETER_REVIEW_INCOM_ANNUITY_ARREAR_UNTAX = "arrears_untaxed";
    public static final String PARAMETER_REVIEW_DEDUCTION_VEHICLES = "vehicles";
    public static final String PARAMETER_REVIEW_DEDUCTION_HOW = "how_related";
    public static final String PARAMETER_REVIEW_DEDUCTION_KM = "km_travelled";
    public static final String PARAMETER_REVIEW_DEDUCTION_TYPE = "type_brand";
    public static final String PARAMETER_REVIEW_DEDUCTION_REG = "reg_number";
    public static final String PARAMETER_REVIEW_AMOUNT = "amount";
    public static final String PARAMETER_REVIEW_TYPE = "type";
    public static int MAX_IMAGE_ATTACH = 9;
    public static final String INTENT_EXTRA_ALBUM = "album_name";
    public static final int REQUEST_CODE_PICK_IMAGE = 357;
    public static final int RESPONSE_CODE_PICK_IMAGE = 753;
    public static final String INTENT_EXTRA_IMAGES = "extra_image";
    public static final int REQUEST_CODE_CAMERA = 567;
    public static final String EXTRA_ONLY_IMAGE = "extra_only_image";
    public static final String EXTRA_IS_CROP_PROFILE = "extra_is_crop_profile";
    public static final String EXTRA_IMAGE_PATH = "extra_image_path";
    public static final int REQUEST_CODE_CROP_IMAGE = 951;
    public static final int RESPONSE_CODE_CROP_IMAGE = 159;

    public static final String COUNT_IMAGE_ATTACH_EXTRA = "extra_count_image";

    public static final String DB_NAME = "hozo";
    public static final String KEY_ENCRYPTION_DEFAULT = "abcdefghabcdefghabcdefghabcdefghabcdefghabcdefghabcdefghabcdefgh";


    public static final int PERMISSION_REQUEST_CODE = 987;
    public static final int PERMISSION_REQUEST_CODE_AVATA = 852;
    public static final String TRANSITION_EXTRA = "transition_extra";

    //update user parameter
    public static final String PARAMETER_EMAIL = "email";
    public static final long MAX_FILE_SIZE = 5; //MB

    public static final int CREATE_APP_REQUEST_CODE = 258;
    public static final int CREATE_APP_RESULT_CODE = 852;


}