begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|legacy
operator|.
name|LegacyNumericRangeQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|BooleanClause
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|BooleanQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|BoostQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|ConstantScoreQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|FuzzyQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|PrefixQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TermQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TermRangeQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|WildcardQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|CharsRefBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|parser
operator|.
name|QueryParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|FieldType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Collection of static utilities useful for query parsing.  *  *  */
end_comment

begin_class
DECL|class|QueryParsing
specifier|public
class|class
name|QueryParsing
block|{
DECL|field|OP
specifier|public
specifier|static
specifier|final
name|String
name|OP
init|=
literal|"q.op"
decl_stmt|;
comment|// the SolrParam used to override the QueryParser "default operator"
DECL|field|V
specifier|public
specifier|static
specifier|final
name|String
name|V
init|=
literal|"v"
decl_stmt|;
comment|// value of this parameter
DECL|field|F
specifier|public
specifier|static
specifier|final
name|String
name|F
init|=
literal|"f"
decl_stmt|;
comment|// field that a query or command pertains to
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"type"
decl_stmt|;
comment|// parser for this query or command
DECL|field|DEFTYPE
specifier|public
specifier|static
specifier|final
name|String
name|DEFTYPE
init|=
literal|"defType"
decl_stmt|;
comment|// default parser for any direct subqueries
DECL|field|SPLIT_ON_WHITESPACE
specifier|public
specifier|static
specifier|final
name|String
name|SPLIT_ON_WHITESPACE
init|=
literal|"sow"
decl_stmt|;
comment|// Whether to split on whitespace prior to analysis
DECL|field|LOCALPARAM_START
specifier|public
specifier|static
specifier|final
name|String
name|LOCALPARAM_START
init|=
literal|"{!"
decl_stmt|;
DECL|field|LOCALPARAM_END
specifier|public
specifier|static
specifier|final
name|char
name|LOCALPARAM_END
init|=
literal|'}'
decl_stmt|;
comment|// true if the value was specified by the "v" param (i.e. v=myval, or v=$param)
DECL|field|VAL_EXPLICIT
specifier|public
specifier|static
specifier|final
name|String
name|VAL_EXPLICIT
init|=
literal|"__VAL_EXPLICIT__"
decl_stmt|;
comment|/**    * Returns the default operator for use by Query Parsers, parsed from the df string    * @param notUsed is not used, but is there for back compat with 3rd party QParsers    * @param df the df string from request    * @deprecated this method is here purely not to break code back compat in 7.x    */
annotation|@
name|Deprecated
DECL|method|getQueryParserDefaultOperator
specifier|public
specifier|static
name|QueryParser
operator|.
name|Operator
name|getQueryParserDefaultOperator
parameter_list|(
specifier|final
name|IndexSchema
name|notUsed
parameter_list|,
specifier|final
name|String
name|df
parameter_list|)
block|{
return|return
name|parseOP
argument_list|(
name|df
argument_list|)
return|;
block|}
comment|/**    * Returns the effective default field based on the 'df' param.    * TODO: This is kept for 3rd party QParser compat in 7.x. Remove this method in Solr 8.0    * @param ignored Not in use    * @param df the default field, which will be returned as-is    * @see org.apache.solr.common.params.CommonParams#DF    * @deprecated IndexScema does not contain defaultField anymore, you must rely on df alone    */
annotation|@
name|Deprecated
DECL|method|getDefaultField
specifier|public
specifier|static
name|String
name|getDefaultField
parameter_list|(
specifier|final
name|IndexSchema
name|ignored
parameter_list|,
specifier|final
name|String
name|df
parameter_list|)
block|{
return|return
name|df
return|;
block|}
comment|/**    * @param txt Text to parse    * @param start Index into text for start of parsing    * @param target Object to inject with parsed settings    * @param params Additional existing parameters    */
DECL|method|parseLocalParams
specifier|public
specifier|static
name|int
name|parseLocalParams
parameter_list|(
name|String
name|txt
parameter_list|,
name|int
name|start
parameter_list|,
name|ModifiableSolrParams
name|target
parameter_list|,
name|SolrParams
name|params
parameter_list|)
throws|throws
name|SyntaxError
block|{
return|return
name|parseLocalParams
argument_list|(
name|txt
argument_list|,
name|start
argument_list|,
name|target
argument_list|,
name|params
argument_list|,
name|LOCALPARAM_START
argument_list|,
name|LOCALPARAM_END
argument_list|)
return|;
block|}
comment|/**    * @param txt Text to parse    * @param start Index into text for start of parsing    * @param target Object to inject with parsed settings    * @param params Additional existing parameters    * @param startString String that indicates the start of a localParams section    * @param endChar Character that indicates the end of a localParams section    */
DECL|method|parseLocalParams
specifier|public
specifier|static
name|int
name|parseLocalParams
parameter_list|(
name|String
name|txt
parameter_list|,
name|int
name|start
parameter_list|,
name|ModifiableSolrParams
name|target
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|String
name|startString
parameter_list|,
name|char
name|endChar
parameter_list|)
throws|throws
name|SyntaxError
block|{
name|int
name|off
init|=
name|start
decl_stmt|;
if|if
condition|(
operator|!
name|txt
operator|.
name|startsWith
argument_list|(
name|startString
argument_list|,
name|off
argument_list|)
condition|)
return|return
name|start
return|;
name|StrParser
name|p
init|=
operator|new
name|StrParser
argument_list|(
name|txt
argument_list|,
name|start
argument_list|,
name|txt
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|p
operator|.
name|pos
operator|+=
name|startString
operator|.
name|length
argument_list|()
expr_stmt|;
comment|// skip over "{!"
for|for
control|(
init|;
condition|;
control|)
block|{
comment|/*       if (p.pos>=txt.length()) {         throw new SyntaxError("Missing '}' parsing local params '" + txt + '"');       }       */
name|char
name|ch
init|=
name|p
operator|.
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
name|ch
operator|==
name|endChar
condition|)
block|{
return|return
name|p
operator|.
name|pos
operator|+
literal|1
return|;
block|}
name|String
name|id
init|=
name|p
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
literal|"Expected ending character '"
operator|+
name|endChar
operator|+
literal|"' parsing local params '"
operator|+
name|txt
operator|+
literal|'"'
argument_list|)
throw|;
block|}
name|String
name|val
init|=
literal|null
decl_stmt|;
name|ch
operator|=
name|p
operator|.
name|peek
argument_list|()
expr_stmt|;
if|if
condition|(
name|ch
operator|!=
literal|'='
condition|)
block|{
comment|// single word... treat {!func} as type=func for easy lookup
name|val
operator|=
name|id
expr_stmt|;
name|id
operator|=
name|TYPE
expr_stmt|;
block|}
else|else
block|{
comment|// saw equals, so read value
name|p
operator|.
name|pos
operator|++
expr_stmt|;
name|ch
operator|=
name|p
operator|.
name|peek
argument_list|()
expr_stmt|;
name|boolean
name|deref
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'$'
condition|)
block|{
name|p
operator|.
name|pos
operator|++
expr_stmt|;
name|ch
operator|=
name|p
operator|.
name|peek
argument_list|()
expr_stmt|;
name|deref
operator|=
literal|true
expr_stmt|;
comment|// dereference whatever value is read by treating it as a variable name
block|}
if|if
condition|(
name|ch
operator|==
literal|'\"'
operator|||
name|ch
operator|==
literal|'\''
condition|)
block|{
name|val
operator|=
name|p
operator|.
name|getQuotedString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// read unquoted literal ended by whitespace or endChar (normally '}')
comment|// there is no escaping.
name|int
name|valStart
init|=
name|p
operator|.
name|pos
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|p
operator|.
name|pos
operator|>=
name|p
operator|.
name|end
condition|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
literal|"Missing end to unquoted value starting at "
operator|+
name|valStart
operator|+
literal|" str='"
operator|+
name|txt
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|char
name|c
init|=
name|p
operator|.
name|val
operator|.
name|charAt
argument_list|(
name|p
operator|.
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
name|endChar
operator|||
name|Character
operator|.
name|isWhitespace
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|val
operator|=
name|p
operator|.
name|val
operator|.
name|substring
argument_list|(
name|valStart
argument_list|,
name|p
operator|.
name|pos
argument_list|)
expr_stmt|;
break|break;
block|}
name|p
operator|.
name|pos
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|deref
condition|)
block|{
comment|// dereference parameter
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|val
operator|=
name|params
operator|.
name|get
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|target
operator|!=
literal|null
condition|)
name|target
operator|.
name|add
argument_list|(
name|id
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * "foo" returns null    * "{!prefix f=myfield}yes" returns type="prefix",f="myfield",v="yes"    * "{!prefix f=myfield v=$p}" returns type="prefix",f="myfield",v=params.get("p")    */
DECL|method|getLocalParams
specifier|public
specifier|static
name|SolrParams
name|getLocalParams
parameter_list|(
name|String
name|txt
parameter_list|,
name|SolrParams
name|params
parameter_list|)
throws|throws
name|SyntaxError
block|{
if|if
condition|(
name|txt
operator|==
literal|null
operator|||
operator|!
name|txt
operator|.
name|startsWith
argument_list|(
name|LOCALPARAM_START
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|ModifiableSolrParams
name|localParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|int
name|start
init|=
name|QueryParsing
operator|.
name|parseLocalParams
argument_list|(
name|txt
argument_list|,
literal|0
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|String
name|val
init|=
name|localParams
operator|.
name|get
argument_list|(
name|V
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|val
operator|=
name|txt
operator|.
name|substring
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|localParams
operator|.
name|set
argument_list|(
name|V
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// localParams.put(VAL_EXPLICIT, "true");
block|}
return|return
name|localParams
return|;
block|}
comment|///////////////////////////
comment|///////////////////////////
comment|///////////////////////////
DECL|method|writeFieldName
specifier|static
name|FieldType
name|writeFieldName
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|Appendable
name|out
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldType
name|ft
init|=
literal|null
decl_stmt|;
name|ft
operator|=
name|schema
operator|.
name|getFieldTypeNoEx
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|ft
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|"(UNKNOWN FIELD "
operator|+
name|name
operator|+
literal|')'
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
return|return
name|ft
return|;
block|}
DECL|method|writeFieldVal
specifier|static
name|void
name|writeFieldVal
parameter_list|(
name|String
name|val
parameter_list|,
name|FieldType
name|ft
parameter_list|,
name|Appendable
name|out
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|ft
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|out
operator|.
name|append
argument_list|(
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|"EXCEPTION(val="
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|append
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeFieldVal
specifier|static
name|void
name|writeFieldVal
parameter_list|(
name|BytesRef
name|val
parameter_list|,
name|FieldType
name|ft
parameter_list|,
name|Appendable
name|out
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|ft
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|CharsRefBuilder
name|readable
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|val
argument_list|,
name|readable
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
name|readable
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|"EXCEPTION(val="
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
name|val
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|append
argument_list|(
name|val
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|FLAG_BOOSTED
specifier|private
specifier|static
name|int
name|FLAG_BOOSTED
init|=
literal|0x01
decl_stmt|;
DECL|field|FLAG_IS_CLAUSE
specifier|private
specifier|static
name|int
name|FLAG_IS_CLAUSE
init|=
literal|0x02
decl_stmt|;
comment|/**    * @see #toString(Query,IndexSchema)    */
DECL|method|toString
specifier|public
specifier|static
name|void
name|toString
parameter_list|(
name|Query
name|query
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|Appendable
name|out
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|subflag
init|=
name|flags
operator|&
operator|~
operator|(
name|FLAG_BOOSTED
operator||
name|FLAG_IS_CLAUSE
operator|)
decl_stmt|;
comment|// clear the boosted / is clause flags for recursion
if|if
condition|(
name|query
operator|instanceof
name|TermQuery
condition|)
block|{
name|TermQuery
name|q
init|=
operator|(
name|TermQuery
operator|)
name|query
decl_stmt|;
name|Term
name|t
init|=
name|q
operator|.
name|getTerm
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
name|writeFieldName
argument_list|(
name|t
operator|.
name|field
argument_list|()
argument_list|,
name|schema
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|writeFieldVal
argument_list|(
name|t
operator|.
name|bytes
argument_list|()
argument_list|,
name|ft
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|TermRangeQuery
condition|)
block|{
name|TermRangeQuery
name|q
init|=
operator|(
name|TermRangeQuery
operator|)
name|query
decl_stmt|;
name|String
name|fname
init|=
name|q
operator|.
name|getField
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
name|writeFieldName
argument_list|(
name|fname
argument_list|,
name|schema
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|out
operator|.
name|append
argument_list|(
name|q
operator|.
name|includesLower
argument_list|()
condition|?
literal|'['
else|:
literal|'{'
argument_list|)
expr_stmt|;
name|BytesRef
name|lt
init|=
name|q
operator|.
name|getLowerTerm
argument_list|()
decl_stmt|;
name|BytesRef
name|ut
init|=
name|q
operator|.
name|getUpperTerm
argument_list|()
decl_stmt|;
if|if
condition|(
name|lt
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeFieldVal
argument_list|(
name|lt
argument_list|,
name|ft
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
expr_stmt|;
if|if
condition|(
name|ut
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeFieldVal
argument_list|(
name|ut
argument_list|,
name|ft
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|append
argument_list|(
name|q
operator|.
name|includesUpper
argument_list|()
condition|?
literal|']'
else|:
literal|'}'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|LegacyNumericRangeQuery
condition|)
block|{
name|LegacyNumericRangeQuery
name|q
init|=
operator|(
name|LegacyNumericRangeQuery
operator|)
name|query
decl_stmt|;
name|String
name|fname
init|=
name|q
operator|.
name|getField
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
name|writeFieldName
argument_list|(
name|fname
argument_list|,
name|schema
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|out
operator|.
name|append
argument_list|(
name|q
operator|.
name|includesMin
argument_list|()
condition|?
literal|'['
else|:
literal|'{'
argument_list|)
expr_stmt|;
name|Number
name|lt
init|=
name|q
operator|.
name|getMin
argument_list|()
decl_stmt|;
name|Number
name|ut
init|=
name|q
operator|.
name|getMax
argument_list|()
decl_stmt|;
if|if
condition|(
name|lt
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|append
argument_list|(
name|lt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
expr_stmt|;
if|if
condition|(
name|ut
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|append
argument_list|(
name|ut
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|append
argument_list|(
name|q
operator|.
name|includesMax
argument_list|()
condition|?
literal|']'
else|:
literal|'}'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|BooleanQuery
name|q
init|=
operator|(
name|BooleanQuery
operator|)
name|query
decl_stmt|;
name|boolean
name|needParens
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|q
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
operator|!=
literal|0
operator|||
operator|(
name|flags
operator|&
operator|(
name|FLAG_IS_CLAUSE
operator||
name|FLAG_BOOSTED
operator|)
operator|)
operator|!=
literal|0
condition|)
block|{
name|needParens
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|needParens
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
block|}
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|BooleanClause
name|c
range|:
name|q
operator|.
name|clauses
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|first
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|c
operator|.
name|isProhibited
argument_list|()
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|isRequired
argument_list|()
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'+'
argument_list|)
expr_stmt|;
block|}
name|Query
name|subQuery
init|=
name|c
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|toString
argument_list|(
name|subQuery
argument_list|,
name|schema
argument_list|,
name|out
argument_list|,
name|subflag
operator||
name|FLAG_IS_CLAUSE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|needParens
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|q
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'~'
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|q
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|PrefixQuery
condition|)
block|{
name|PrefixQuery
name|q
init|=
operator|(
name|PrefixQuery
operator|)
name|query
decl_stmt|;
name|Term
name|prefix
init|=
name|q
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
name|writeFieldName
argument_list|(
name|prefix
operator|.
name|field
argument_list|()
argument_list|,
name|schema
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|out
operator|.
name|append
argument_list|(
name|prefix
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|WildcardQuery
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|query
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|FuzzyQuery
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|query
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|ConstantScoreQuery
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|query
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|WrappedQuery
condition|)
block|{
name|WrappedQuery
name|q
init|=
operator|(
name|WrappedQuery
operator|)
name|query
decl_stmt|;
name|out
operator|.
name|append
argument_list|(
name|q
operator|.
name|getOptions
argument_list|()
argument_list|)
expr_stmt|;
name|toString
argument_list|(
name|q
operator|.
name|getWrappedQuery
argument_list|()
argument_list|,
name|schema
argument_list|,
name|out
argument_list|,
name|subflag
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|BoostQuery
condition|)
block|{
name|BoostQuery
name|q
init|=
operator|(
name|BoostQuery
operator|)
name|query
decl_stmt|;
name|toString
argument_list|(
name|q
operator|.
name|getQuery
argument_list|()
argument_list|,
name|schema
argument_list|,
name|out
argument_list|,
name|subflag
operator||
name|FLAG_BOOSTED
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|"^"
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
name|Float
operator|.
name|toString
argument_list|(
name|q
operator|.
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|append
argument_list|(
name|query
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|'('
operator|+
name|query
operator|.
name|toString
argument_list|()
operator|+
literal|')'
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Formats a Query for debugging, using the IndexSchema to make    * complex field types readable.    *<p>    * The benefit of using this method instead of calling    *<code>Query.toString</code> directly is that it knows about the data    * types of each field, so any field which is encoded in a particularly    * complex way is still readable. The downside is that it only knows    * about built in Query types, and will not be able to format custom    * Query classes.    *</p>    */
DECL|method|toString
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|Query
name|query
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
try|try
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|toString
argument_list|(
name|query
argument_list|,
name|schema
argument_list|,
name|sb
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Builds a list of String which are stringified versions of a list of Queries    */
DECL|method|toString
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|toString
parameter_list|(
name|List
argument_list|<
name|Query
argument_list|>
name|queries
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|out
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|queries
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Query
name|q
range|:
name|queries
control|)
block|{
name|out
operator|.
name|add
argument_list|(
name|QueryParsing
operator|.
name|toString
argument_list|(
name|q
argument_list|,
name|schema
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
comment|/**    * Parses default operator string into Operator object    * @param operator the string from request    * @return Operator.AND if string equals "AND", else return Operator.OR (default)    */
DECL|method|parseOP
specifier|public
specifier|static
name|QueryParser
operator|.
name|Operator
name|parseOP
parameter_list|(
name|String
name|operator
parameter_list|)
block|{
return|return
literal|"and"
operator|.
name|equalsIgnoreCase
argument_list|(
name|operator
argument_list|)
condition|?
name|QueryParser
operator|.
name|Operator
operator|.
name|AND
else|:
name|QueryParser
operator|.
name|Operator
operator|.
name|OR
return|;
block|}
block|}
end_class

end_unit

