# SAML Integration

SAML Integration enables the customer to get control of user management for their organization instead of using Appranix's default user management. Only the account admin can enable this feature.

### To configure SAML Identity Provider in your Appranix Account

1. Click **My Account** in navigation bar
2. Click **Single Sign On** in My Account Page.
3. Check the **Enable Single Sign-On** check box.
4. Select **Provider Type**
5. Appranix will provide the following details to register in Identity Provider

  1. Entity ID
  2. Assertion Consumer Service(ACS) URL
  3. Encryption Certificate
6. You will need to submit the following fields to Appranix for registering the  Identity Provider

  1. Entity ID
  2. Single-Sign-On Service URL.
  3. Signing Certificate

### Appranix will raise the SAML Request to the Identity Provider in the following Standard

```
Authentication Context = "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport"
Name ID Policy = "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress"
Sign-On Request binding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"
Protocol namespace = "urn:oasis:names:tc:SAML:2.0:protocol"
Assertion namespace = "urn:oasis:names:tc:SAML:2.0:assertion"
```

### SAML Response should contain the following fields for successful validation

  - SAML Protocol version should 2.0
  - SAML Response or Assertion should be signed.
  - SAML Response should have an email attribute.
