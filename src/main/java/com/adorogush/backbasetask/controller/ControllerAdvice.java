/*
* Copyright 2020 Aleksandr Dorogush
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.adorogush.backbasetask.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** A centralized handler for exceptions. */
@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {
  private static final Logger log = LogManager.getLogger();
  private static final String SERVER_ERROR_MSG =
      "The server encountered an error when trying to fulfill the request.";

  @ExceptionHandler
  public ResponseEntity<String> statusCode(final ResponseStatusException ex) {
    log.debug("Returning {} {}", ex.getStatus(), ex.getReason());
    return ResponseEntity.status(ex.getStatus()).body(ex.getReason());
  }

  @ExceptionHandler
  public ResponseEntity<String> internalError(final Exception ex) {
    log.error("Returning 500", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(SERVER_ERROR_MSG);
  }
}
