package com.revature.units;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import io.javalin.Javalin;
import com.revature.MainDriver;

@ExtendWith(MockitoExtension.class)
public class MainDriverUnitTest {

    @Mock
    private Javalin app;

    @InjectMocks
    private MainDriver mainDriver;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMain() {
        try (MockedStatic<Javalin> javalinMockedStatic = mockStatic(Javalin.class)) {
            // Mock Javalin.create() to return the mocked app
            javalinMockedStatic.when(() -> Javalin.create(any())).thenReturn(app);

            // Mock app.start() to return the app instance
            when(app.start(anyInt())).thenReturn(app);

            // Call the main method
            MainDriver.main(new String[] {});

            // Verify that the necessary methods were called
            verify(app).start(7000);
        }
    }
}
