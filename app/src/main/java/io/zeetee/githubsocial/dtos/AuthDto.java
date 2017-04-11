package io.zeetee.githubsocial.dtos;

import io.zeetee.githubsocial.utils.GSConstants;

/**
 * By GT.
 */

public class AuthDto {

    public String[] scopes = {"public_repo","user"};
    public String note = GSConstants.Github.note;
    public String client_id = GSConstants.Github.client_id;
    public String client_secret = GSConstants.Github.client_secret;

}
