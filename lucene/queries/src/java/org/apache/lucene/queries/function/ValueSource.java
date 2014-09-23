begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queries.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|LeafReaderContext
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
name|FieldComparator
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
name|FieldComparatorSource
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
name|IndexSearcher
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
name|SortField
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
name|IdentityHashMap
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
comment|/**  * Instantiates {@link FunctionValues} for a particular reader.  *<br>  * Often used when creating a {@link FunctionQuery}.  *  *  */
end_comment

begin_class
DECL|class|ValueSource
specifier|public
specifier|abstract
class|class
name|ValueSource
block|{
comment|/**    * Gets the values for this reader and the context that was previously    * passed to createWeight()    */
DECL|method|getValues
specifier|public
specifier|abstract
name|FunctionValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|equals
specifier|public
specifier|abstract
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|abstract
name|int
name|hashCode
parameter_list|()
function_decl|;
comment|/**    * description of field, used in explain()    */
DECL|method|description
specifier|public
specifier|abstract
name|String
name|description
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|description
argument_list|()
return|;
block|}
comment|/**    * Implementations should propagate createWeight to sub-ValueSources which can optionally store    * weight info in the context. The context object will be passed to getValues()    * where this info can be retrieved.    */
DECL|method|createWeight
specifier|public
name|void
name|createWeight
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{   }
comment|/**    * Returns a new non-threadsafe context map.    */
DECL|method|newContext
specifier|public
specifier|static
name|Map
name|newContext
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
block|{
name|Map
name|context
init|=
operator|new
name|IdentityHashMap
argument_list|()
decl_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"searcher"
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
return|return
name|context
return|;
block|}
comment|//
comment|// Sorting by function
comment|//
comment|/**    * EXPERIMENTAL: This method is subject to change.    *<p>    * Get the SortField for this ValueSource.  Uses the {@link #getValues(java.util.Map, org.apache.lucene.index.LeafReaderContext)}    * to populate the SortField.    *    * @param reverse true if this is a reverse sort.    * @return The {@link org.apache.lucene.search.SortField} for the ValueSource    */
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|boolean
name|reverse
parameter_list|)
block|{
return|return
operator|new
name|ValueSourceSortField
argument_list|(
name|reverse
argument_list|)
return|;
block|}
DECL|class|ValueSourceSortField
class|class
name|ValueSourceSortField
extends|extends
name|SortField
block|{
DECL|method|ValueSourceSortField
specifier|public
name|ValueSourceSortField
parameter_list|(
name|boolean
name|reverse
parameter_list|)
block|{
name|super
argument_list|(
name|description
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|REWRITEABLE
argument_list|,
name|reverse
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|SortField
name|rewrite
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
name|context
init|=
name|newContext
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
return|return
operator|new
name|SortField
argument_list|(
name|getField
argument_list|()
argument_list|,
operator|new
name|ValueSourceComparatorSource
argument_list|(
name|context
argument_list|)
argument_list|,
name|getReverse
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|ValueSourceComparatorSource
class|class
name|ValueSourceComparatorSource
extends|extends
name|FieldComparatorSource
block|{
DECL|field|context
specifier|private
specifier|final
name|Map
name|context
decl_stmt|;
DECL|method|ValueSourceComparatorSource
specifier|public
name|ValueSourceComparatorSource
parameter_list|(
name|Map
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newComparator
specifier|public
name|FieldComparator
argument_list|<
name|Double
argument_list|>
name|newComparator
parameter_list|(
name|String
name|fieldname
parameter_list|,
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|,
name|boolean
name|reversed
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ValueSourceComparator
argument_list|(
name|context
argument_list|,
name|numHits
argument_list|)
return|;
block|}
block|}
comment|/**    * Implement a {@link org.apache.lucene.search.FieldComparator} that works    * off of the {@link FunctionValues} for a ValueSource    * instead of the normal Lucene FieldComparator that works off of a FieldCache.    */
DECL|class|ValueSourceComparator
class|class
name|ValueSourceComparator
extends|extends
name|FieldComparator
argument_list|<
name|Double
argument_list|>
block|{
DECL|field|values
specifier|private
specifier|final
name|double
index|[]
name|values
decl_stmt|;
DECL|field|docVals
specifier|private
name|FunctionValues
name|docVals
decl_stmt|;
DECL|field|bottom
specifier|private
name|double
name|bottom
decl_stmt|;
DECL|field|fcontext
specifier|private
specifier|final
name|Map
name|fcontext
decl_stmt|;
DECL|field|topValue
specifier|private
name|double
name|topValue
decl_stmt|;
DECL|method|ValueSourceComparator
name|ValueSourceComparator
parameter_list|(
name|Map
name|fcontext
parameter_list|,
name|int
name|numHits
parameter_list|)
block|{
name|this
operator|.
name|fcontext
operator|=
name|fcontext
expr_stmt|;
name|values
operator|=
operator|new
name|double
index|[
name|numHits
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slot1
parameter_list|,
name|int
name|slot2
parameter_list|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|values
index|[
name|slot1
index|]
argument_list|,
name|values
index|[
name|slot2
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|bottom
argument_list|,
name|docVals
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
name|values
index|[
name|slot
index|]
operator|=
name|docVals
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|FieldComparator
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|docVals
operator|=
name|getValues
argument_list|(
name|fcontext
argument_list|,
name|context
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
specifier|final
name|int
name|bottom
parameter_list|)
block|{
name|this
operator|.
name|bottom
operator|=
name|values
index|[
name|bottom
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setTopValue
specifier|public
name|void
name|setTopValue
parameter_list|(
specifier|final
name|Double
name|value
parameter_list|)
block|{
name|this
operator|.
name|topValue
operator|=
name|value
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|Double
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|values
index|[
name|slot
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|compareTop
specifier|public
name|int
name|compareTop
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|double
name|docValue
init|=
name|docVals
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|Double
operator|.
name|compare
argument_list|(
name|topValue
argument_list|,
name|docValue
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

