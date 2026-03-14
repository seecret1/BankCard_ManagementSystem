package com.github.seecret1.bank_card_management_system.service;

import com.github.seecret1.bank_card_management_system.entity.User;

public interface InternalUserService {

    User findUserEntityByCriterial(String criterial);
}
