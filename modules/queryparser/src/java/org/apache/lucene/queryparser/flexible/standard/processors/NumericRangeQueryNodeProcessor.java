begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.processors
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|processors
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|messages
operator|.
name|MessageImpl
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|QueryNodeException
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|QueryNodeParseException
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|config
operator|.
name|FieldConfig
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|config
operator|.
name|QueryConfigHandler
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|messages
operator|.
name|QueryParserMessages
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|FieldQueryNode
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|QueryNode
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|processors
operator|.
name|QueryNodeProcessorImpl
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|util
operator|.
name|StringUtils
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
operator|.
name|NumericConfig
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
operator|.
name|StandardQueryConfigHandler
operator|.
name|ConfigurationKeys
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|nodes
operator|.
name|NumericQueryNode
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|nodes
operator|.
name|NumericRangeQueryNode
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|nodes
operator|.
name|TermRangeQueryNode
import|;
end_import

begin_comment
comment|/**  * This processor is used to convert {@link TermRangeQueryNode}s to  * {@link NumericRangeQueryNode}s. It looks for  * {@link ConfigurationKeys#NUMERIC_CONFIG} set in the {@link FieldConfig} of  * every {@link TermRangeQueryNode} found. If  * {@link ConfigurationKeys#NUMERIC_CONFIG} is found, it considers that  * {@link TermRangeQueryNode} to be a numeric range query and convert it to  * {@link NumericRangeQueryNode}.  *   * @see ConfigurationKeys#NUMERIC_CONFIG  * @see TermRangeQueryNode  * @see NumericConfig  * @see NumericRangeQueryNode  */
end_comment

begin_class
DECL|class|NumericRangeQueryNodeProcessor
specifier|public
class|class
name|NumericRangeQueryNodeProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
comment|/**    * Constructs an empty {@link NumericRangeQueryNode} object.    */
DECL|method|NumericRangeQueryNodeProcessor
specifier|public
name|NumericRangeQueryNodeProcessor
parameter_list|()
block|{
comment|// empty constructor
block|}
annotation|@
name|Override
DECL|method|postProcessNode
specifier|protected
name|QueryNode
name|postProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|node
operator|instanceof
name|TermRangeQueryNode
condition|)
block|{
name|QueryConfigHandler
name|config
init|=
name|getQueryConfigHandler
argument_list|()
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|TermRangeQueryNode
name|termRangeNode
init|=
operator|(
name|TermRangeQueryNode
operator|)
name|node
decl_stmt|;
name|FieldConfig
name|fieldConfig
init|=
name|config
operator|.
name|getFieldConfig
argument_list|(
name|StringUtils
operator|.
name|toString
argument_list|(
name|termRangeNode
operator|.
name|getField
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldConfig
operator|!=
literal|null
condition|)
block|{
name|NumericConfig
name|numericConfig
init|=
name|fieldConfig
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|NUMERIC_CONFIG
argument_list|)
decl_stmt|;
if|if
condition|(
name|numericConfig
operator|!=
literal|null
condition|)
block|{
name|FieldQueryNode
name|lower
init|=
name|termRangeNode
operator|.
name|getLowerBound
argument_list|()
decl_stmt|;
name|FieldQueryNode
name|upper
init|=
name|termRangeNode
operator|.
name|getUpperBound
argument_list|()
decl_stmt|;
name|String
name|lowerText
init|=
name|lower
operator|.
name|getTextAsString
argument_list|()
decl_stmt|;
name|String
name|upperText
init|=
name|upper
operator|.
name|getTextAsString
argument_list|()
decl_stmt|;
name|NumberFormat
name|numberFormat
init|=
name|numericConfig
operator|.
name|getNumberFormat
argument_list|()
decl_stmt|;
name|Number
name|lowerNumber
init|=
literal|null
decl_stmt|,
name|upperNumber
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|lowerText
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|lowerNumber
operator|=
name|numberFormat
operator|.
name|parse
argument_list|(
name|lowerText
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryNodeParseException
argument_list|(
operator|new
name|MessageImpl
argument_list|(
name|QueryParserMessages
operator|.
name|COULD_NOT_PARSE_NUMBER
argument_list|,
name|lower
operator|.
name|getTextAsString
argument_list|()
argument_list|,
name|numberFormat
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|upperText
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|upperNumber
operator|=
name|numberFormat
operator|.
name|parse
argument_list|(
name|upperText
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryNodeParseException
argument_list|(
operator|new
name|MessageImpl
argument_list|(
name|QueryParserMessages
operator|.
name|COULD_NOT_PARSE_NUMBER
argument_list|,
name|upper
operator|.
name|getTextAsString
argument_list|()
argument_list|,
name|numberFormat
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
switch|switch
condition|(
name|numericConfig
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|LONG
case|:
if|if
condition|(
name|upperNumber
operator|!=
literal|null
condition|)
name|upperNumber
operator|=
name|upperNumber
operator|.
name|longValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|lowerNumber
operator|!=
literal|null
condition|)
name|lowerNumber
operator|=
name|lowerNumber
operator|.
name|longValue
argument_list|()
expr_stmt|;
break|break;
case|case
name|INT
case|:
if|if
condition|(
name|upperNumber
operator|!=
literal|null
condition|)
name|upperNumber
operator|=
name|upperNumber
operator|.
name|intValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|lowerNumber
operator|!=
literal|null
condition|)
name|lowerNumber
operator|=
name|lowerNumber
operator|.
name|intValue
argument_list|()
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
if|if
condition|(
name|upperNumber
operator|!=
literal|null
condition|)
name|upperNumber
operator|=
name|upperNumber
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|lowerNumber
operator|!=
literal|null
condition|)
name|lowerNumber
operator|=
name|lowerNumber
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
if|if
condition|(
name|upperNumber
operator|!=
literal|null
condition|)
name|upperNumber
operator|=
name|upperNumber
operator|.
name|floatValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|lowerNumber
operator|!=
literal|null
condition|)
name|lowerNumber
operator|=
name|lowerNumber
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
name|NumericQueryNode
name|lowerNode
init|=
operator|new
name|NumericQueryNode
argument_list|(
name|termRangeNode
operator|.
name|getField
argument_list|()
argument_list|,
name|lowerNumber
argument_list|,
name|numberFormat
argument_list|)
decl_stmt|;
name|NumericQueryNode
name|upperNode
init|=
operator|new
name|NumericQueryNode
argument_list|(
name|termRangeNode
operator|.
name|getField
argument_list|()
argument_list|,
name|upperNumber
argument_list|,
name|numberFormat
argument_list|)
decl_stmt|;
name|boolean
name|lowerInclusive
init|=
name|termRangeNode
operator|.
name|isLowerInclusive
argument_list|()
decl_stmt|;
name|boolean
name|upperInclusive
init|=
name|termRangeNode
operator|.
name|isUpperInclusive
argument_list|()
decl_stmt|;
return|return
operator|new
name|NumericRangeQueryNode
argument_list|(
name|lowerNode
argument_list|,
name|upperNode
argument_list|,
name|lowerInclusive
argument_list|,
name|upperInclusive
argument_list|,
name|numericConfig
argument_list|)
return|;
block|}
block|}
block|}
block|}
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|preProcessNode
specifier|protected
name|QueryNode
name|preProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|setChildrenOrder
specifier|protected
name|List
argument_list|<
name|QueryNode
argument_list|>
name|setChildrenOrder
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|children
return|;
block|}
block|}
end_class

end_unit

