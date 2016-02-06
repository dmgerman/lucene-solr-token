begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.bbox
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|bbox
package|;
end_package

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|ValueSource
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
name|docvalues
operator|.
name|DoubleDocValues
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
name|Explanation
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
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Rectangle
import|;
end_import

begin_comment
comment|/**  * A base class for calculating a spatial relevance rank per document from a provided  * {@link ValueSource} in which {@link FunctionValues#objectVal(int)} returns a {@link  * com.spatial4j.core.shape.Rectangle}.  *<p>  * Implementers: remember to implement equals and hashCode if you have  * fields!  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BBoxSimilarityValueSource
specifier|public
specifier|abstract
class|class
name|BBoxSimilarityValueSource
extends|extends
name|ValueSource
block|{
DECL|field|bboxValueSource
specifier|private
specifier|final
name|ValueSource
name|bboxValueSource
decl_stmt|;
DECL|method|BBoxSimilarityValueSource
specifier|public
name|BBoxSimilarityValueSource
parameter_list|(
name|ValueSource
name|bboxValueSource
parameter_list|)
block|{
name|this
operator|.
name|bboxValueSource
operator|=
name|bboxValueSource
expr_stmt|;
block|}
annotation|@
name|Override
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
block|{
name|bboxValueSource
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|bboxValueSource
operator|.
name|description
argument_list|()
operator|+
literal|","
operator|+
name|similarityDescription
argument_list|()
operator|+
literal|")"
return|;
block|}
comment|/** A comma-separated list of configurable items of the subclass to put into {@link #description()}. */
DECL|method|similarityDescription
specifier|protected
specifier|abstract
name|String
name|similarityDescription
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|getValues
specifier|public
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
block|{
specifier|final
name|FunctionValues
name|shapeValues
init|=
name|bboxValueSource
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
return|return
operator|new
name|DoubleDocValues
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
comment|//? limit to Rect or call getBoundingBox()? latter would encourage bad practice
specifier|final
name|Rectangle
name|rect
init|=
operator|(
name|Rectangle
operator|)
name|shapeValues
operator|.
name|objectVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|rect
operator|==
literal|null
condition|?
literal|0
else|:
name|score
argument_list|(
name|rect
argument_list|,
literal|null
argument_list|)
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
name|shapeValues
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|Rectangle
name|rect
init|=
operator|(
name|Rectangle
operator|)
name|shapeValues
operator|.
name|objectVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|rect
operator|==
literal|null
condition|)
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"no rect"
argument_list|)
return|;
name|AtomicReference
argument_list|<
name|Explanation
argument_list|>
name|explanation
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
name|score
argument_list|(
name|rect
argument_list|,
name|explanation
argument_list|)
expr_stmt|;
return|return
name|explanation
operator|.
name|get
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/**    * Return a relevancy score. If {@code exp} is provided then diagnostic information is added.    * @param rect The indexed rectangle; not null.    * @param exp Optional diagnostic holder.    * @return a score.    */
DECL|method|score
specifier|protected
specifier|abstract
name|double
name|score
parameter_list|(
name|Rectangle
name|rect
parameter_list|,
name|AtomicReference
argument_list|<
name|Explanation
argument_list|>
name|exp
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
comment|//same class
name|BBoxSimilarityValueSource
name|that
init|=
operator|(
name|BBoxSimilarityValueSource
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|bboxValueSource
operator|.
name|equals
argument_list|(
name|that
operator|.
name|bboxValueSource
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|bboxValueSource
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

