begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|text
operator|.
name|ParsePosition
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  *<p>  * A {@link Transformer} instance which can extract numbers out of strings. It uses  * {@link NumberFormat} class to parse strings and supports  * Number, Integer, Currency and Percent styles as supported by  * {@link NumberFormat} with configurable locales.  *</p>  *<p/>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|NumberFormatTransformer
specifier|public
class|class
name|NumberFormatTransformer
extends|extends
name|Transformer
block|{
DECL|field|localeRegex
specifier|private
specifier|static
specifier|final
name|Pattern
name|localeRegex
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^([a-z]{2})-([A-Z]{2})$"
argument_list|)
decl_stmt|;
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fld
range|:
name|context
operator|.
name|getAllEntityFields
argument_list|()
control|)
block|{
name|String
name|style
init|=
name|context
operator|.
name|replaceTokens
argument_list|(
name|fld
operator|.
name|get
argument_list|(
name|FORMAT_STYLE
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|style
operator|!=
literal|null
condition|)
block|{
name|String
name|column
init|=
name|fld
operator|.
name|get
argument_list|(
name|DataImporter
operator|.
name|COLUMN
argument_list|)
decl_stmt|;
name|String
name|srcCol
init|=
name|fld
operator|.
name|get
argument_list|(
name|RegexTransformer
operator|.
name|SRC_COL_NAME
argument_list|)
decl_stmt|;
name|Locale
name|locale
init|=
literal|null
decl_stmt|;
name|String
name|localeStr
init|=
name|context
operator|.
name|replaceTokens
argument_list|(
name|fld
operator|.
name|get
argument_list|(
name|LOCALE
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcCol
operator|==
literal|null
condition|)
name|srcCol
operator|=
name|column
expr_stmt|;
if|if
condition|(
name|localeStr
operator|!=
literal|null
condition|)
block|{
name|Matcher
name|matcher
init|=
name|localeRegex
operator|.
name|matcher
argument_list|(
name|localeStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|()
operator|&&
name|matcher
operator|.
name|groupCount
argument_list|()
operator|==
literal|2
condition|)
block|{
name|locale
operator|=
operator|new
name|Locale
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Invalid Locale specified for field: "
operator|+
name|fld
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|locale
operator|=
name|Locale
operator|.
name|ROOT
expr_stmt|;
block|}
name|Object
name|val
init|=
name|row
operator|.
name|get
argument_list|(
name|srcCol
argument_list|)
decl_stmt|;
name|String
name|styleSmall
init|=
name|style
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|List
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|inputs
init|=
operator|(
name|List
operator|)
name|val
decl_stmt|;
name|List
name|results
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|input
range|:
name|inputs
control|)
block|{
try|try
block|{
name|results
operator|.
name|add
argument_list|(
name|process
argument_list|(
name|input
argument_list|,
name|styleSmall
argument_list|,
name|locale
argument_list|)
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
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Failed to apply NumberFormat on column: "
operator|+
name|column
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|row
operator|.
name|put
argument_list|(
name|column
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|val
operator|==
literal|null
operator|||
name|val
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
continue|continue;
try|try
block|{
name|row
operator|.
name|put
argument_list|(
name|column
argument_list|,
name|process
argument_list|(
name|val
operator|.
name|toString
argument_list|()
argument_list|,
name|styleSmall
argument_list|,
name|locale
argument_list|)
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
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Failed to apply NumberFormat on column: "
operator|+
name|column
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|return
name|row
return|;
block|}
DECL|method|process
specifier|private
name|Number
name|process
parameter_list|(
name|String
name|val
parameter_list|,
name|String
name|style
parameter_list|,
name|Locale
name|locale
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|INTEGER
operator|.
name|equals
argument_list|(
name|style
argument_list|)
condition|)
block|{
return|return
name|parseNumber
argument_list|(
name|val
argument_list|,
name|NumberFormat
operator|.
name|getIntegerInstance
argument_list|(
name|locale
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|NUMBER
operator|.
name|equals
argument_list|(
name|style
argument_list|)
condition|)
block|{
return|return
name|parseNumber
argument_list|(
name|val
argument_list|,
name|NumberFormat
operator|.
name|getNumberInstance
argument_list|(
name|locale
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|CURRENCY
operator|.
name|equals
argument_list|(
name|style
argument_list|)
condition|)
block|{
return|return
name|parseNumber
argument_list|(
name|val
argument_list|,
name|NumberFormat
operator|.
name|getCurrencyInstance
argument_list|(
name|locale
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|PERCENT
operator|.
name|equals
argument_list|(
name|style
argument_list|)
condition|)
block|{
return|return
name|parseNumber
argument_list|(
name|val
argument_list|,
name|NumberFormat
operator|.
name|getPercentInstance
argument_list|(
name|locale
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|parseNumber
specifier|private
name|Number
name|parseNumber
parameter_list|(
name|String
name|val
parameter_list|,
name|NumberFormat
name|numFormat
parameter_list|)
throws|throws
name|ParseException
block|{
name|ParsePosition
name|parsePos
init|=
operator|new
name|ParsePosition
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Number
name|num
init|=
name|numFormat
operator|.
name|parse
argument_list|(
name|val
argument_list|,
name|parsePos
argument_list|)
decl_stmt|;
if|if
condition|(
name|parsePos
operator|.
name|getIndex
argument_list|()
operator|!=
name|val
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"illegal number format"
argument_list|,
name|parsePos
operator|.
name|getIndex
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|num
return|;
block|}
DECL|field|FORMAT_STYLE
specifier|public
specifier|static
specifier|final
name|String
name|FORMAT_STYLE
init|=
literal|"formatStyle"
decl_stmt|;
DECL|field|LOCALE
specifier|public
specifier|static
specifier|final
name|String
name|LOCALE
init|=
literal|"locale"
decl_stmt|;
DECL|field|NUMBER
specifier|public
specifier|static
specifier|final
name|String
name|NUMBER
init|=
literal|"number"
decl_stmt|;
DECL|field|PERCENT
specifier|public
specifier|static
specifier|final
name|String
name|PERCENT
init|=
literal|"percent"
decl_stmt|;
DECL|field|INTEGER
specifier|public
specifier|static
specifier|final
name|String
name|INTEGER
init|=
literal|"integer"
decl_stmt|;
DECL|field|CURRENCY
specifier|public
specifier|static
specifier|final
name|String
name|CURRENCY
init|=
literal|"currency"
decl_stmt|;
block|}
end_class

end_unit

