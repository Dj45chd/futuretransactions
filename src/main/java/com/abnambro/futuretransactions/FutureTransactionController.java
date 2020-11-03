package com.abnambro.futuretransactions;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class FutureTransactionController {

    private static final String MEDIA_TYPE_CSV = "text/csv";

    @Autowired
    FutureTransactionService futureTransactionService;

    @RequestMapping(value = "/futuretransactions", method = RequestMethod.GET, produces = MEDIA_TYPE_CSV)
    @ApiOperation("Get All the future transactions for the customer")
    @ResponseStatus(HttpStatus.OK)
    public void getFutureTransactions(HttpServletResponse response) throws IOException {

        futureTransactionService.getAllTransaction(response);
    }
}
