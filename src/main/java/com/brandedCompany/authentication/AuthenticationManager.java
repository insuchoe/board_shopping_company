package com.brandedCompany.authentication;

import com.brandedCompany.authentication.Authentication;
import com.brandedCompany.authentication.exception.*;

public interface AuthenticationManager
{
    void addAuthentication(Authentication authentication) throws AuthenticationRuntimeException;
    void removeAuthentication(Authentication authentication) throws AuthenticationNotFoundException;
    void refreshAuthentication(Authentication authentication) throws AuthenticationRuntimeException;

    boolean hasAuthentication(Authentication authentication);

}
