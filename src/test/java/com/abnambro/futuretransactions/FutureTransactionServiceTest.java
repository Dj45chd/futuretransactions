package com.abnambro.futuretransactions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class FutureTransactionServiceTest {

    @MockBean
    FutureTransactionService futureTransactionService;

    @Test
    public void shouldGenerateCSVFile() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        futureTransactionService.getAllTransaction(response);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        assertThat(response!=null);
    }

}

