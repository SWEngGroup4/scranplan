package com.group4sweng.scranplan;

import java.io.Serializable;

public class UserInfoPublic extends UserBase implements Serializable {

    enum Privacy {
        PUBLIC,
        RESTRICTED,
        PRIVATE
    }


}
