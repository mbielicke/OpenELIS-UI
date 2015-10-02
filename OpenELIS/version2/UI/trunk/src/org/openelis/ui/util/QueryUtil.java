package org.openelis.ui.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.QueryFieldUtil;

public class QueryUtil {
	
	CriteriaBuilder builder;
	CriteriaQuery<?> query;
	Root<?> root;
	
	public QueryUtil(CriteriaBuilder builder, CriteriaQuery<?> query, Root<?> root) {
		this.builder = builder;
		this.query = query;
		this.root = root;
	}
	
	public Predicate[] createQuery(List<QueryData> qds) {
		ArrayList<Predicate> predicates = new ArrayList<Predicate>();
		
		for (QueryData qd : qds) {
			predicates.add(createPredicate(getPath(root,qd.getKey()),qd));
		}
		return (Predicate[])predicates.toArray();
//		return qds.stream().map(qd -> createPredicate(getPath(root,qd.getKey()),qd))
//					       .collect(Collectors.toList())
//					       .toArray(new Predicate[]{});
	}
	    
    public <T extends Comparable<? super T>> Predicate createPredicate(Expression<? extends T> path, QueryData qd) {
    	QueryFieldUtil field = new QueryFieldUtil();
    	try {
    		field.parse(qd.getQuery());
    	} catch (Exception e) {
    		return null;
    	}
        Iterator<String> fieldCompIt = field.getComparator().iterator();
        Iterator<String> fieldParamIt = field.getParameter().iterator();
        ArrayList<Predicate> predicates = new ArrayList<>();
        while (fieldCompIt.hasNext()) {
            String comp = (String)fieldCompIt.next();
            String param = (String)fieldParamIt.next();
            switch (comp) {
            	case "!" :
            	case "!=" : 
            		predicates.add(builder.notEqual(path,getValue(qd.getType(),param)));
            		break;
            	case "~" :
            	case "like " : 
            		predicates.add(builder.like((Path<String>)path, param));
            		break;
            	case "(" :
            		String[] list = param.split(",");
            		predicates.add(path.in(getValueList(qd.getType(),list)));
            		break;
            	case "between " :
            		String[] ends = param.split("\\.\\.");
            		predicates.add(builder.<T>between(path, (Expression<? extends T>)getValue(qd.getType(),ends[0]), (Expression<? extends T>)getValue(qd.getType(),ends[1])));
            		break;
            	case "<" :
            		predicates.add(builder.<T>lessThan(path, (Expression<? extends T>)getValue(qd.getType(),param)));
            		break;
            	case ">" :
            		predicates.add(builder.<T>greaterThan(path, (Expression<? extends T>)getValue(qd.getType(),param)));
            		break;
            	case "<=" :
            		predicates.add(builder.<T>lessThanOrEqualTo(path, (Expression<? extends T>)getValue(qd.getType(),param)));
            		break;
            	case ">=" :
            		predicates.add(builder.<T>greaterThanOrEqualTo(path, (Expression<? extends T>)getValue(qd.getType(),param)));
            		break;
            	default :
            		if(param.equals("NULL")){
            			if(comp.startsWith("!"))
            				predicates.add(builder.isNotNull(path));
            			else
            				predicates.add(builder.isNull(path));
            		} else {
            			predicates.add(builder.equal(path, (Object)getValue(qd.getType(),param)));
            		}
            }
        }
        return builder.or(predicates.toArray(new Predicate[]{}));
    }
    
    @SuppressWarnings({ "unchecked", "deprecation" })
	public <T> T getValue(QueryData.Type type, String param) {
    	switch (type) {
    		case INTEGER :
    			return (T)new Integer(param);
    		case DOUBLE :
    			return (T)new Double(param);
    		case DATE :
    			return (T)new Date(param.replaceAll("-","/"));
    		default :
    			return (T)param;
    	}
    }
    
    public List<Object> getValueList(QueryData.Type type, String[] params) {
    	ArrayList<Object> values = new ArrayList<>();
    	for (String param : params) {
    		values.add(getValue(type,param));
    	}
    	return values;
    }
    
    public <T extends Comparable<? super T>> Path<T> getPath(From<?,?> root, String path) {
    	if (path.contains(".")) {
    		String[] split = path.split("\\.",2);
    		return getPath(root.join(split[0]),split[1]);
    	}
    	return root.get(path);
    }

}
