package org.openelis.ui.util;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.invocation.InvocationMarker;
import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.internal.util.StringJoiner;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.verification.VerificationMode;
import org.openelis.ui.widget.ScreenWidgetInt;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;

public class TestingUtil {
    
    public static void verifyEnabled(ScreenWidgetInt... mocks) {
        assert mocks != null : "Method does not accept null";
        for(ScreenWidgetInt mock : mocks)
            verify(mock,last).setEnabled(true);
    }
    
    public static void verifyNotEnabled(ScreenWidgetInt... mocks) {
        assert mocks != null : "Method does not accept null";
        for(ScreenWidgetInt mock : mocks)
            verify(mock,last).setEnabled(false);
    }
    
    public static <T> void verifyValue(HasValue<T> mock, T value) {
        assert mock != null : "Mock object can not be null";
        verify(mock,last).setValue(value);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Answer<Void> createAnswerWithResult(final T result) {
        return new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {            
                AsyncCallback<T> callback = (AsyncCallback<T>) invocation.getArguments()[invocation.getArguments().length-1];
                callback.onSuccess(result);
                return null;
            }
        };
    }
    
    @SuppressWarnings("rawtypes")
    public static Answer<Void> createAnswerWithFailure(final Throwable caught) {
        return new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AsyncCallback callback = (AsyncCallback) invocation.getArguments()[invocation.getArguments().length-1];
                callback.onFailure(caught);
                return null;
            }
        };
    }
    
    public static void mockNamedQueryWithSingleResult(EntityManager manager, String queryName, Object data) {
        when(mockNamedQuery(queryName,manager).getSingleResult()).thenReturn(data);
    }
    
    public static void mockNamedQueryThatThrowsException(EntityManager manager, String queryName, Exception exception) {
        when(mockNamedQuery(queryName,manager).getSingleResult()).thenThrow(exception);
    }
    
    public static void mockNamedQueryWithResultList(EntityManager manager, String queryName, List<?> result) {
        when(mockNamedQuery(queryName,manager).getResultList()).thenReturn(result);
    }
    
    public static void mockQueryWithResultList(EntityManager manager, List<?> result) {
        when(mockQuery(manager).getResultList()).thenReturn(result);
    }
    
    public static Query mockNamedQuery(String queryId,EntityManager manager) {
        Query query = mock(Query.class);
        when(manager.createNamedQuery(queryId)).thenReturn(query);
        return query;
    }
    
    public static Query mockQuery(EntityManager manager) {
        Query query = mock(Query.class);
        when(manager.createQuery(anyString())).thenReturn(query);
        return query;
    }
    
    private static Last last = new Last();
    
    public static Last last() {
        return last;
    }

    private static class Last implements VerificationMode {
        
        private final InvocationMarker invocationMarker = new InvocationMarker();
        
        @Override
        public void verify(VerificationData data) {
            List<Invocation> invocations = data.getAllInvocations();
            InvocationMatcher wanted = data.getWanted();
            
            Invocation last = null;
            for(Invocation inv : invocations) {
                if(inv.getMethod().equals(wanted.getMethod()))
                    last = inv;
            }
           
            if(last == null)
                new org.mockito.exceptions.Reporter().wantedButNotInvoked(wanted);
            
            if(wanted.matches(last)) {
                invocationMarker.markVerified(last, wanted);
            }else
                throw new MockitoException(StringJoiner.join("Expected "+wanted.toString()+"to be last call", "but found "+last.toString()+" instead"));
        }
        
    }
}
