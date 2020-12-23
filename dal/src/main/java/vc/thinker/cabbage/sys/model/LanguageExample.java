package vc.thinker.cabbage.sys.model;

import com.sinco.mybatis.dal.core.AbstractExample;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vc.thinker.cabbage.sys.bo.LanguageBO;

public class LanguageExample extends AbstractExample<LanguageBO> {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    protected String limit;

    public LanguageExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public void setLimit(int count) {
        this.limit = String.valueOf(count);
    }

    public void setLimit(int offset, int rows) {
        this.limit = new StringBuilder().append(String.valueOf(offset)).append(",").append(String.valueOf(rows)).toString();
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andLanguageNameIsNull() {
            addCriterion("language_name is null");
            return (Criteria) this;
        }

        public Criteria andLanguageNameIsNotNull() {
            addCriterion("language_name is not null");
            return (Criteria) this;
        }

        public Criteria andLanguageNameEqualTo(String value) {
            addCriterion("language_name =", value, "languageName");
            return (Criteria) this;
        }

        public Criteria andLanguageNameNotEqualTo(String value) {
            addCriterion("language_name <>", value, "languageName");
            return (Criteria) this;
        }

        public Criteria andLanguageNameGreaterThan(String value) {
            addCriterion("language_name >", value, "languageName");
            return (Criteria) this;
        }

        public Criteria andLanguageNameGreaterThanOrEqualTo(String value) {
            addCriterion("language_name >=", value, "languageName");
            return (Criteria) this;
        }

        public Criteria andLanguageNameLessThan(String value) {
            addCriterion("language_name <", value, "languageName");
            return (Criteria) this;
        }

        public Criteria andLanguageNameLessThanOrEqualTo(String value) {
            addCriterion("language_name <=", value, "languageName");
            return (Criteria) this;
        }

        public Criteria andLanguageNameLike(String value) {
            addCriterion("language_name like", value, "languageName");
            return (Criteria) this;
        }

        public Criteria andLanguageNameNotLike(String value) {
            addCriterion("language_name not like", value, "languageName");
            return (Criteria) this;
        }

        public Criteria andLanguageNameIn(List<String> values) {
            addCriterion("language_name in", values, "languageName");
            return (Criteria) this;
        }

        public Criteria andLanguageNameNotIn(List<String> values) {
            addCriterion("language_name not in", values, "languageName");
            return (Criteria) this;
        }

        public Criteria andLanguageNameBetween(String value1, String value2) {
            addCriterion("language_name between", value1, value2, "languageName");
            return (Criteria) this;
        }

        public Criteria andLanguageNameNotBetween(String value1, String value2) {
            addCriterion("language_name not between", value1, value2, "languageName");
            return (Criteria) this;
        }

        public Criteria andDefaultLanguageIsNull() {
            addCriterion("default_language is null");
            return (Criteria) this;
        }

        public Criteria andDefaultLanguageIsNotNull() {
            addCriterion("default_language is not null");
            return (Criteria) this;
        }

        public Criteria andDefaultLanguageEqualTo(String value) {
            addCriterion("default_language =", value, "defaultLanguage");
            return (Criteria) this;
        }

        public Criteria andDefaultLanguageNotEqualTo(String value) {
            addCriterion("default_language <>", value, "defaultLanguage");
            return (Criteria) this;
        }

        public Criteria andDefaultLanguageGreaterThan(String value) {
            addCriterion("default_language >", value, "defaultLanguage");
            return (Criteria) this;
        }

        public Criteria andDefaultLanguageGreaterThanOrEqualTo(String value) {
            addCriterion("default_language >=", value, "defaultLanguage");
            return (Criteria) this;
        }

        public Criteria andDefaultLanguageLessThan(String value) {
            addCriterion("default_language <", value, "defaultLanguage");
            return (Criteria) this;
        }

        public Criteria andDefaultLanguageLessThanOrEqualTo(String value) {
            addCriterion("default_language <=", value, "defaultLanguage");
            return (Criteria) this;
        }

        public Criteria andDefaultLanguageLike(String value) {
            addCriterion("default_language like", value, "defaultLanguage");
            return (Criteria) this;
        }

        public Criteria andDefaultLanguageNotLike(String value) {
            addCriterion("default_language not like", value, "defaultLanguage");
            return (Criteria) this;
        }

        public Criteria andDefaultLanguageIn(List<String> values) {
            addCriterion("default_language in", values, "defaultLanguage");
            return (Criteria) this;
        }

        public Criteria andDefaultLanguageNotIn(List<String> values) {
            addCriterion("default_language not in", values, "defaultLanguage");
            return (Criteria) this;
        }

        public Criteria andDefaultLanguageBetween(String value1, String value2) {
            addCriterion("default_language between", value1, value2, "defaultLanguage");
            return (Criteria) this;
        }

        public Criteria andDefaultLanguageNotBetween(String value1, String value2) {
            addCriterion("default_language not between", value1, value2, "defaultLanguage");
            return (Criteria) this;
        }

        public Criteria andLanguageDescIsNull() {
            addCriterion("language_desc is null");
            return (Criteria) this;
        }

        public Criteria andLanguageDescIsNotNull() {
            addCriterion("language_desc is not null");
            return (Criteria) this;
        }

        public Criteria andLanguageDescEqualTo(String value) {
            addCriterion("language_desc =", value, "languageDesc");
            return (Criteria) this;
        }

        public Criteria andLanguageDescNotEqualTo(String value) {
            addCriterion("language_desc <>", value, "languageDesc");
            return (Criteria) this;
        }

        public Criteria andLanguageDescGreaterThan(String value) {
            addCriterion("language_desc >", value, "languageDesc");
            return (Criteria) this;
        }

        public Criteria andLanguageDescGreaterThanOrEqualTo(String value) {
            addCriterion("language_desc >=", value, "languageDesc");
            return (Criteria) this;
        }

        public Criteria andLanguageDescLessThan(String value) {
            addCriterion("language_desc <", value, "languageDesc");
            return (Criteria) this;
        }

        public Criteria andLanguageDescLessThanOrEqualTo(String value) {
            addCriterion("language_desc <=", value, "languageDesc");
            return (Criteria) this;
        }

        public Criteria andLanguageDescLike(String value) {
            addCriterion("language_desc like", value, "languageDesc");
            return (Criteria) this;
        }

        public Criteria andLanguageDescNotLike(String value) {
            addCriterion("language_desc not like", value, "languageDesc");
            return (Criteria) this;
        }

        public Criteria andLanguageDescIn(List<String> values) {
            addCriterion("language_desc in", values, "languageDesc");
            return (Criteria) this;
        }

        public Criteria andLanguageDescNotIn(List<String> values) {
            addCriterion("language_desc not in", values, "languageDesc");
            return (Criteria) this;
        }

        public Criteria andLanguageDescBetween(String value1, String value2) {
            addCriterion("language_desc between", value1, value2, "languageDesc");
            return (Criteria) this;
        }

        public Criteria andLanguageDescNotBetween(String value1, String value2) {
            addCriterion("language_desc not between", value1, value2, "languageDesc");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andSortIsNull() {
            addCriterion("sort is null");
            return (Criteria) this;
        }

        public Criteria andSortIsNotNull() {
            addCriterion("sort is not null");
            return (Criteria) this;
        }

        public Criteria andSortEqualTo(Integer value) {
            addCriterion("sort =", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortNotEqualTo(Integer value) {
            addCriterion("sort <>", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortGreaterThan(Integer value) {
            addCriterion("sort >", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortGreaterThanOrEqualTo(Integer value) {
            addCriterion("sort >=", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortLessThan(Integer value) {
            addCriterion("sort <", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortLessThanOrEqualTo(Integer value) {
            addCriterion("sort <=", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortIn(List<Integer> values) {
            addCriterion("sort in", values, "sort");
            return (Criteria) this;
        }

        public Criteria andSortNotIn(List<Integer> values) {
            addCriterion("sort not in", values, "sort");
            return (Criteria) this;
        }

        public Criteria andSortBetween(Integer value1, Integer value2) {
            addCriterion("sort between", value1, value2, "sort");
            return (Criteria) this;
        }

        public Criteria andSortNotBetween(Integer value1, Integer value2) {
            addCriterion("sort not between", value1, value2, "sort");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}