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
name|common
operator|.
name|mutable
operator|.
name|MutableValue
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
name|common
operator|.
name|mutable
operator|.
name|MutableValueLong
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|queries
operator|.
name|function
operator|.
name|DocValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSourceScorer
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
name|Bits
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
name|cache
operator|.
name|LongValuesCreator
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
name|cache
operator|.
name|CachedArray
operator|.
name|LongValues
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Obtains float field values from the {@link org.apache.lucene.search.FieldCache}  * using<code>getFloats()</code>  * and makes those values available as other numeric types, casting as needed.  *  *  */
end_comment

begin_class
DECL|class|LongFieldSource
specifier|public
class|class
name|LongFieldSource
extends|extends
name|NumericFieldCacheSource
argument_list|<
name|LongValues
argument_list|>
block|{
DECL|method|LongFieldSource
specifier|public
name|LongFieldSource
parameter_list|(
name|LongValuesCreator
name|creator
parameter_list|)
block|{
name|super
argument_list|(
name|creator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"long("
operator|+
name|field
operator|+
literal|')'
return|;
block|}
DECL|method|externalToLong
specifier|public
name|long
name|externalToLong
parameter_list|(
name|String
name|extVal
parameter_list|)
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|extVal
argument_list|)
return|;
block|}
DECL|method|longToObject
specifier|public
name|Object
name|longToObject
parameter_list|(
name|long
name|val
parameter_list|)
block|{
return|return
name|val
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LongValues
name|vals
init|=
name|cache
operator|.
name|getLongs
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|,
name|field
argument_list|,
name|creator
argument_list|)
decl_stmt|;
specifier|final
name|long
index|[]
name|arr
init|=
name|vals
operator|.
name|values
decl_stmt|;
specifier|final
name|Bits
name|valid
init|=
name|vals
operator|.
name|valid
decl_stmt|;
return|return
operator|new
name|LongDocValues
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|arr
index|[
name|doc
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|valid
operator|.
name|get
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|objectVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|valid
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|?
name|longToObject
argument_list|(
name|arr
index|[
name|doc
index|]
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
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
name|long
name|lower
decl_stmt|,
name|upper
decl_stmt|;
comment|// instead of using separate comparison functions, adjust the endpoints.
if|if
condition|(
name|lowerVal
operator|==
literal|null
condition|)
block|{
name|lower
operator|=
name|Long
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
else|else
block|{
name|lower
operator|=
name|externalToLong
argument_list|(
name|lowerVal
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|includeLower
operator|&&
name|lower
operator|<
name|Long
operator|.
name|MAX_VALUE
condition|)
name|lower
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|upperVal
operator|==
literal|null
condition|)
block|{
name|upper
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|upper
operator|=
name|externalToLong
argument_list|(
name|upperVal
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|includeUpper
operator|&&
name|upper
operator|>
name|Long
operator|.
name|MIN_VALUE
condition|)
name|upper
operator|--
expr_stmt|;
block|}
specifier|final
name|long
name|ll
init|=
name|lower
decl_stmt|;
specifier|final
name|long
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
name|long
name|val
init|=
name|arr
index|[
name|doc
index|]
decl_stmt|;
comment|// only check for deleted if it's the default value
comment|// if (val==0&& reader.isDeleted(doc)) return false;
return|return
name|val
operator|>=
name|ll
operator|&&
name|val
operator|<=
name|uu
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValueFiller
name|getValueFiller
parameter_list|()
block|{
return|return
operator|new
name|ValueFiller
argument_list|()
block|{
specifier|private
specifier|final
name|long
index|[]
name|longArr
init|=
name|arr
decl_stmt|;
specifier|private
specifier|final
name|MutableValueLong
name|mval
init|=
name|newMutableValueLong
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|MutableValue
name|getValue
parameter_list|()
block|{
return|return
name|mval
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|fillValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|mval
operator|.
name|value
operator|=
name|longArr
index|[
name|doc
index|]
expr_stmt|;
name|mval
operator|.
name|exists
operator|=
name|valid
operator|.
name|get
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
DECL|method|newMutableValueLong
specifier|protected
name|MutableValueLong
name|newMutableValueLong
parameter_list|()
block|{
return|return
operator|new
name|MutableValueLong
argument_list|()
return|;
block|}
block|}
end_class

end_unit

