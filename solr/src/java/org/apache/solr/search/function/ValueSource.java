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
name|Scorer
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
name|index
operator|.
name|MultiFields
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
name|io
operator|.
name|Serializable
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_comment
comment|/**  * Instantiates {@link org.apache.solr.search.function.DocValues} for a particular reader.  *<br>  * Often used when creating a {@link FunctionQuery}.  *  * @version $Id$  */
end_comment

begin_class
DECL|class|ValueSource
specifier|public
specifier|abstract
class|class
name|ValueSource
implements|implements
name|Serializable
block|{
comment|/**    * Gets the values for this reader and the context that was previously    * passed to createWeight()    */
DECL|method|getValues
specifier|public
specifier|abstract
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
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
comment|/**    * EXPERIMENTAL: This method is subject to change.    *<br>WARNING: Sorted function queries are not currently weighted.    *<p>    * Get the SortField for this ValueSource.  Uses the {@link #getValues(java.util.Map, org.apache.lucene.index.IndexReader)}    * to populate the SortField.    *     * @param reverse true if this is a reverse sort.    * @return The {@link org.apache.lucene.search.SortField} for the ValueSource    * @throws IOException if there was a problem reading the values.    */
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|boolean
name|reverse
parameter_list|)
throws|throws
name|IOException
block|{
comment|//should we pass in the description for the field name?
comment|//Hmm, Lucene is going to intern whatever we pass in, not sure I like that
comment|//and we can't pass in null, either, as that throws an illegal arg. exception
return|return
operator|new
name|SortField
argument_list|(
name|description
argument_list|()
argument_list|,
operator|new
name|ValueSourceComparatorSource
argument_list|()
argument_list|,
name|reverse
argument_list|)
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
parameter_list|()
block|{
return|return
operator|new
name|IdentityHashMap
argument_list|()
return|;
block|}
DECL|class|ValueSourceComparatorSource
class|class
name|ValueSourceComparatorSource
extends|extends
name|FieldComparatorSource
block|{
DECL|method|ValueSourceComparatorSource
specifier|public
name|ValueSourceComparatorSource
parameter_list|()
block|{      }
DECL|method|newComparator
specifier|public
name|FieldComparator
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
name|numHits
argument_list|)
return|;
block|}
block|}
comment|/**    * Implement a {@link org.apache.lucene.search.FieldComparator} that works    * off of the {@link org.apache.solr.search.function.DocValues} for a ValueSource    * instead of the normal Lucene FieldComparator that works off of a FieldCache.    */
DECL|class|ValueSourceComparator
class|class
name|ValueSourceComparator
extends|extends
name|FieldComparator
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
name|DocValues
name|docVals
decl_stmt|;
DECL|field|bottom
specifier|private
name|double
name|bottom
decl_stmt|;
DECL|method|ValueSourceComparator
name|ValueSourceComparator
parameter_list|(
name|int
name|numHits
parameter_list|)
block|{
name|values
operator|=
operator|new
name|double
index|[
name|numHits
index|]
expr_stmt|;
block|}
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
specifier|final
name|double
name|v1
init|=
name|values
index|[
name|slot1
index|]
decl_stmt|;
specifier|final
name|double
name|v2
init|=
name|values
index|[
name|slot2
index|]
decl_stmt|;
if|if
condition|(
name|v1
operator|>
name|v2
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|v1
operator|<
name|v2
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|double
name|v2
init|=
name|docVals
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|bottom
operator|>
name|v2
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|bottom
operator|<
name|v2
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
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
DECL|method|setNextReader
specifier|public
name|FieldComparator
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|docVals
operator|=
name|getValues
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|reader
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
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
DECL|method|value
specifier|public
name|Comparable
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|Double
operator|.
name|valueOf
argument_list|(
name|values
index|[
name|slot
index|]
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_class
DECL|class|ValueSourceScorer
class|class
name|ValueSourceScorer
extends|extends
name|Scorer
block|{
DECL|field|reader
specifier|protected
name|IndexReader
name|reader
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|maxDoc
specifier|protected
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|values
specifier|protected
specifier|final
name|DocValues
name|values
decl_stmt|;
DECL|field|checkDeletes
specifier|protected
name|boolean
name|checkDeletes
decl_stmt|;
DECL|field|delDocs
specifier|private
specifier|final
name|Bits
name|delDocs
decl_stmt|;
DECL|method|ValueSourceScorer
specifier|protected
name|ValueSourceScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|DocValues
name|values
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|setCheckDeletes
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|delDocs
operator|=
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|getReader
specifier|public
name|IndexReader
name|getReader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
DECL|method|setCheckDeletes
specifier|public
name|void
name|setCheckDeletes
parameter_list|(
name|boolean
name|checkDeletes
parameter_list|)
block|{
name|this
operator|.
name|checkDeletes
operator|=
name|checkDeletes
operator|&&
name|reader
operator|.
name|hasDeletions
argument_list|()
expr_stmt|;
block|}
DECL|method|matches
specifier|public
name|boolean
name|matches
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
operator|!
name|checkDeletes
operator|||
operator|!
name|delDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|)
operator|&&
name|matchesValue
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|matchesValue
specifier|public
name|boolean
name|matchesValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|doc
operator|++
expr_stmt|;
if|if
condition|(
name|doc
operator|>=
name|maxDoc
condition|)
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
if|if
condition|(
name|matches
argument_list|(
name|doc
argument_list|)
condition|)
return|return
name|doc
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
comment|// also works fine when target==NO_MORE_DOCS
name|doc
operator|=
name|target
operator|-
literal|1
expr_stmt|;
return|return
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|values
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
end_class

end_unit

