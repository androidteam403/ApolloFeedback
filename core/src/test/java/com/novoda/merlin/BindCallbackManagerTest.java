package com.novoda.merlin;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BindCallbackManagerTest {

    private final NetworkStatus networkStatus = mock(NetworkStatus.class);
    private final Register<BindableInterface> bindables = new Register<>();

    private BindCallbackManager bindCallbackManager;

    @Before
    public void setUp() {
        bindCallbackManager = new BindCallbackManager(bindables);
    }

    @Test
    public void givenRegisteredConnectable_whenCallingOnConnect_thenCallsConnectForConnectable() {
        BindableInterface bindable = givenRegisteredBindable();

        bindCallbackManager.onMerlinBind(networkStatus);

        verify(bindable).onBind(networkStatus);
    }

    @Test
    public void givenMultipleRegisteredConnectables_whenCallingOnConnect_thenCallsConnectForAllConnectables() {
        List<BindableInterface> bindables = givenMultipleRegisteredBindables();

        bindCallbackManager.onMerlinBind(networkStatus);

        for (BindableInterface bindable : bindables) {
            verify(bindable).onBind(networkStatus);
        }
    }

    private List<BindableInterface> givenMultipleRegisteredBindables() {
        List<BindableInterface> bindables = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            BindableInterface connectable = mock(BindableInterface.class);
            bindables.add(connectable);
            this.bindables.register(connectable);
        }
        return bindables;
    }

    private BindableInterface givenRegisteredBindable() {
        BindableInterface bindable = mock(BindableInterface.class);
        bindables.register(bindable);
        return bindable;
    }

}
