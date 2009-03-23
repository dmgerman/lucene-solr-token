begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
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
name|search
operator|.
name|FieldCache
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
name|ExtendedFieldCache
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
name|index
operator|.
name|IndexReader
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

begin_comment
comment|/** Internal class, subject to change.  *  Serves as base class for DocValues based on StringIndex   **/
end_comment

begin_class
DECL|class|StringIndexDocValues
specifier|public
specifier|abstract
class|class
name|StringIndexDocValues
extends|extends
name|DocValues
block|{
DECL|field|index
specifier|protected
specifier|final
name|FieldCache
operator|.
name|StringIndex
name|index
decl_stmt|;
DECL|field|order
specifier|protected
specifier|final
name|int
index|[]
name|order
decl_stmt|;
DECL|field|lookup
specifier|protected
specifier|final
name|String
index|[]
name|lookup
decl_stmt|;
DECL|field|vs
specifier|protected
specifier|final
name|ValueSource
name|vs
decl_stmt|;
DECL|method|StringIndexDocValues
specifier|public
name|StringIndexDocValues
parameter_list|(
name|ValueSource
name|vs
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|index
operator|=
name|ExtendedFieldCache
operator|.
name|EXT_DEFAULT
operator|.
name|getStringIndex
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|order
operator|=
name|index
operator|.
name|order
expr_stmt|;
name|lookup
operator|=
name|index
operator|.
name|lookup
expr_stmt|;
name|this
operator|.
name|vs
operator|=
name|vs
expr_stmt|;
block|}
DECL|method|toTerm
specifier|protected
specifier|abstract
name|String
name|toTerm
parameter_list|(
name|String
name|readableValue
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|getRangeScorer
specifier|public
name|ValueSourceScorer
name|getRangeScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|lowerVal
parameter_list|,
name|String
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
comment|// TODO: are lowerVal and upperVal in indexed form or not?
name|lowerVal
operator|=
name|lowerVal
operator|==
literal|null
condition|?
literal|null
else|:
name|toTerm
argument_list|(
name|lowerVal
argument_list|)
expr_stmt|;
name|upperVal
operator|=
name|upperVal
operator|==
literal|null
condition|?
literal|null
else|:
name|toTerm
argument_list|(
name|upperVal
argument_list|)
expr_stmt|;
name|int
name|lower
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
if|if
condition|(
name|lowerVal
operator|!=
literal|null
condition|)
block|{
name|lower
operator|=
name|index
operator|.
name|binarySearchLookup
argument_list|(
name|lowerVal
argument_list|)
expr_stmt|;
if|if
condition|(
name|lower
operator|<
literal|0
condition|)
block|{
name|lower
operator|=
operator|-
name|lower
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|includeLower
condition|)
block|{
name|lower
operator|++
expr_stmt|;
block|}
block|}
name|int
name|upper
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
if|if
condition|(
name|upperVal
operator|!=
literal|null
condition|)
block|{
name|upper
operator|=
name|index
operator|.
name|binarySearchLookup
argument_list|(
name|upperVal
argument_list|)
expr_stmt|;
if|if
condition|(
name|upper
operator|<
literal|0
condition|)
block|{
name|upper
operator|=
operator|-
name|upper
operator|-
literal|2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|includeUpper
condition|)
block|{
name|upper
operator|--
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|ll
init|=
name|lower
decl_stmt|;
specifier|final
name|int
name|uu
init|=
name|upper
decl_stmt|;
return|return
operator|new
name|ValueSourceScorer
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matchesValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|int
name|ord
init|=
name|order
index|[
name|doc
index|]
decl_stmt|;
return|return
name|ord
operator|>=
name|ll
operator|&&
name|ord
operator|<=
name|uu
return|;
block|}
block|}
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|vs
operator|.
name|description
argument_list|()
operator|+
literal|'='
operator|+
name|strVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
end_class

end_unit

