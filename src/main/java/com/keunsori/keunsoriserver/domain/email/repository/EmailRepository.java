package com.keunsori.keunsoriserver.domain.email.repository;

import org.springframework.data.repository.CrudRepository;

import com.keunsori.keunsoriserver.domain.email.domain.EmailAuthentication;

public interface EmailRepository extends CrudRepository<EmailAuthentication, Long> {}
