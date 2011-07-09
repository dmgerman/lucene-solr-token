begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.core.messages
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|core
operator|.
name|messages
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|messages
operator|.
name|NLS
import|;
end_import

begin_comment
comment|/**  * Flexible Query Parser message bundle class  */
end_comment

begin_class
DECL|class|QueryParserMessages
specifier|public
class|class
name|QueryParserMessages
extends|extends
name|NLS
block|{
DECL|field|BUNDLE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|BUNDLE_NAME
init|=
name|QueryParserMessages
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|method|QueryParserMessages
specifier|private
name|QueryParserMessages
parameter_list|()
block|{
comment|// Do not instantiate
block|}
static|static
block|{
comment|// register all string ids with NLS class and initialize static string
comment|// values
name|NLS
operator|.
name|initializeMessages
argument_list|(
name|BUNDLE_NAME
argument_list|,
name|QueryParserMessages
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|// static string must match the strings in the property files.
DECL|field|INVALID_SYNTAX
specifier|public
specifier|static
name|String
name|INVALID_SYNTAX
decl_stmt|;
DECL|field|INVALID_SYNTAX_CANNOT_PARSE
specifier|public
specifier|static
name|String
name|INVALID_SYNTAX_CANNOT_PARSE
decl_stmt|;
DECL|field|INVALID_SYNTAX_FUZZY_LIMITS
specifier|public
specifier|static
name|String
name|INVALID_SYNTAX_FUZZY_LIMITS
decl_stmt|;
DECL|field|INVALID_SYNTAX_FUZZY_EDITS
specifier|public
specifier|static
name|String
name|INVALID_SYNTAX_FUZZY_EDITS
decl_stmt|;
DECL|field|INVALID_SYNTAX_ESCAPE_UNICODE_TRUNCATION
specifier|public
specifier|static
name|String
name|INVALID_SYNTAX_ESCAPE_UNICODE_TRUNCATION
decl_stmt|;
DECL|field|INVALID_SYNTAX_ESCAPE_CHARACTER
specifier|public
specifier|static
name|String
name|INVALID_SYNTAX_ESCAPE_CHARACTER
decl_stmt|;
DECL|field|INVALID_SYNTAX_ESCAPE_NONE_HEX_UNICODE
specifier|public
specifier|static
name|String
name|INVALID_SYNTAX_ESCAPE_NONE_HEX_UNICODE
decl_stmt|;
DECL|field|NODE_ACTION_NOT_SUPPORTED
specifier|public
specifier|static
name|String
name|NODE_ACTION_NOT_SUPPORTED
decl_stmt|;
DECL|field|PARAMETER_VALUE_NOT_SUPPORTED
specifier|public
specifier|static
name|String
name|PARAMETER_VALUE_NOT_SUPPORTED
decl_stmt|;
DECL|field|LUCENE_QUERY_CONVERSION_ERROR
specifier|public
specifier|static
name|String
name|LUCENE_QUERY_CONVERSION_ERROR
decl_stmt|;
DECL|field|EMPTY_MESSAGE
specifier|public
specifier|static
name|String
name|EMPTY_MESSAGE
decl_stmt|;
DECL|field|WILDCARD_NOT_SUPPORTED
specifier|public
specifier|static
name|String
name|WILDCARD_NOT_SUPPORTED
decl_stmt|;
DECL|field|TOO_MANY_BOOLEAN_CLAUSES
specifier|public
specifier|static
name|String
name|TOO_MANY_BOOLEAN_CLAUSES
decl_stmt|;
DECL|field|LEADING_WILDCARD_NOT_ALLOWED
specifier|public
specifier|static
name|String
name|LEADING_WILDCARD_NOT_ALLOWED
decl_stmt|;
DECL|field|COULD_NOT_PARSE_NUMBER
specifier|public
specifier|static
name|String
name|COULD_NOT_PARSE_NUMBER
decl_stmt|;
DECL|field|NUMBER_CLASS_NOT_SUPPORTED_BY_NUMERIC_RANGE_QUERY
specifier|public
specifier|static
name|String
name|NUMBER_CLASS_NOT_SUPPORTED_BY_NUMERIC_RANGE_QUERY
decl_stmt|;
DECL|field|UNSUPPORTED_NUMERIC_DATA_TYPE
specifier|public
specifier|static
name|String
name|UNSUPPORTED_NUMERIC_DATA_TYPE
decl_stmt|;
block|}
end_class

end_unit

