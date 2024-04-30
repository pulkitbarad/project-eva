package metadata

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 10Apr2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object AccountMetadata {


  /*
  * Sample Json structure:
  *
    {
      "TenantId": "tenant2",
      "TenantName": "TenantName",
      "TenantDisplayName": "TenantDisplayName",
      "AccountUsers": [
        {
          "UserId": "UserId",
          "UserName": "UserName",
          "UserDisplayName": "UserDisplayName",
          "ContactDetails": {
            "PrimaryEmail": "PrimaryEmail",
            "SecondaryEmail": "SecondaryEmail",
            "PrimaryTelephone": {
              "CountryCode": "CountryCode",
              "Telephone": "Telephone",
              "Extension": "Extension",
              "IsMobile": "IsMobile"
            },
            "SecondaryTelephone": {
              "CountryCode": "CountryCode",
              "Telephone": "Telephone",
              "Extension": "Extension",
              "IsMobile": "IsMobile"
            },
            "CompanyDetails": {
              "CompanyName": "CompanyName",
              "RegistrationNumber": "RegistrationNumber",
              "TaxNumber": "TaxNumber",
              "Website": "Website"
            }
          }
        }
      ],
      "Address": {
        "AddressLine1": "AddressLine1",
        "AddressLine2": "AddressLine2",
        "City": "City",
        "StateOrProvince": "StateOrProvince",
        "Country": "Country",
        "PostalCode": "PostalCode"
      },
      "MaximumTotalUsageLimitEUR": "MaximumTotalUsageLimitEUR",
      "GcpAccountDetails": {
        "ProjectId": "geometric-watch-153714",
        "Region": "us-central1"
      },
      "AwsAccountDetails": {
        "AwsAccessKey": "AwsAccessKey",
        "AwsSecretKey": "AwsSecretKey",
        "Region": "Region"
      }
    }
  * */

  case class NuoCanvasAccounts(var Accounts: List[NuoCanvasAccount])

  case class NuoCanvasAccount(var TenantId: String,
                              var ProjectId: String)

  case class AccountDetails(var TenantId: String,
                            var TenantName: Option[String],
                            var TenantDisplayName: Option[String],
                            var CompanyDetails: Option[CompanyDetails],
                            //                            var PaymentDetails: PaymentDetails,
                            var Address: Option[Address],
                            var MaximumTotalUsageLimitEUR: Option[String],
                            var PricingPlanCode: Option[String],
                            var AccountUsers: List[AccountUser],
                            var GcpAccountDetails: Option[GcpDetails],
                            var AwsAccountDetails: Option[AwsDetails])

  case class AccountUser(var UserId: String,
                         var UserName: Option[String],
                         var UserDisplayName: Option[String],
                         var ContactDetails: ContactDetails)

  case class ContactDetails(var PrimaryEmail: String,
                            var SecondaryEmail: Option[String],
                            var PrimaryTelephone: Option[Telephone],
                            var SecondaryTelephone: Option[Telephone])

  case class Telephone(var CountryCode: Option[String],
                       var Telephone: Option[String],
                       var Extension: Option[String],
                       var IsMobile: Option[String])

  case class Address(var AddressLine1: Option[String],
                     var AddressLine2: Option[String],
                     var City: Option[String],
                     var StateOrProvince: Option[String],
                     var Country: Option[String],
                     var PostalCode: Option[String])

  case class CompanyDetails(var CompanyName: Option[String],
                            var RegistrationNumber: Option[String],
                            var TaxNumber: Option[String],
                            var Website: Option[String],
                            var PrimaryAccount: Option[AccountUser])

  case class PaymentDetails(var CreditCardHolderName: String,
                            var CreditCardNumber: String,
                            var ExpiryMonth: String,
                            var ExpiryYear: String,
                            var Cvc: String)

  case class GcpDetails(var ProjectId: Option[String],
                        var Location: Option[String])

  //"US" OR "EU". each dataset can be created in different region.

  case class AwsDetails(var AwsAccessKey: Option[String],
                        var AwsSecretKey: Option[String],
                        var Region: Option[String])

  def getNewAccountDetails(tenantId: String,
                           userId: String,
                           emailAddress: String,
                           userName: Option[String],
                           userDisplayName: Option[String] /*,
                           creditCardHolderName: String,
                           creditCardNumber: String,
                           expiryMonth: String,
                           expiryYear: String,
                           cvc: String*/): AccountDetails = {

    AccountDetails(TenantId = tenantId,
      TenantName = None,
      TenantDisplayName = None,
      CompanyDetails = None,
      //      PaymentDetails = getNewPaymentDetails(creditCardHolderName, creditCardNumber, expiryMonth, expiryYear, cvc),
      Address = None,
      MaximumTotalUsageLimitEUR = None,
      PricingPlanCode = None,
      AccountUsers = List(getNewAccountUser(userId, emailAddress, userName, userDisplayName)),
      GcpAccountDetails = None,
      AwsAccountDetails = None)
  }

  def getNewAccountUser(userId: String,
                        emailAddress: String,
                        userName: Option[String],
                        userDisplayName: Option[String]): AccountUser = {

    AccountUser(UserId = userId, UserName = userName, UserDisplayName = userDisplayName, ContactDetails = ContactDetails(PrimaryEmail = emailAddress,
      SecondaryEmail = None,
      PrimaryTelephone = None,
      SecondaryTelephone = None))
  }

  def getNewPaymentDetails(creditCardHolderName: String,
                           creditCardNumber: String,
                           expiryMonth: String,
                           expiryYear: String,
                           cvc: String): PaymentDetails = {

    PaymentDetails(creditCardHolderName, creditCardNumber, expiryMonth, expiryYear, cvc)
  }


}
