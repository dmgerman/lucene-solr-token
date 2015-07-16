begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
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
name|FilterLeafReader
operator|.
name|FilterPostingsEnum
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
name|LeafReader
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
name|index
operator|.
name|PostingsEnum
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
name|Terms
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
name|TermsEnum
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
name|Filter
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|SpatialPrefixTree
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
name|BitSet
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
name|util
operator|.
name|DocIdSetBuilder
import|;
end_import

begin_comment
comment|/**  * Base class for Lucene Filters on SpatialPrefixTree fields.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|AbstractPrefixTreeFilter
specifier|public
specifier|abstract
class|class
name|AbstractPrefixTreeFilter
extends|extends
name|Filter
block|{
DECL|field|queryShape
specifier|protected
specifier|final
name|Shape
name|queryShape
decl_stmt|;
DECL|field|fieldName
specifier|protected
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|grid
specifier|protected
specifier|final
name|SpatialPrefixTree
name|grid
decl_stmt|;
comment|//not in equals/hashCode since it's implied for a specific field
DECL|field|detailLevel
specifier|protected
specifier|final
name|int
name|detailLevel
decl_stmt|;
DECL|method|AbstractPrefixTreeFilter
specifier|public
name|AbstractPrefixTreeFilter
parameter_list|(
name|Shape
name|queryShape
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|SpatialPrefixTree
name|grid
parameter_list|,
name|int
name|detailLevel
parameter_list|)
block|{
name|this
operator|.
name|queryShape
operator|=
name|queryShape
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|grid
operator|=
name|grid
expr_stmt|;
name|this
operator|.
name|detailLevel
operator|=
name|detailLevel
expr_stmt|;
block|}
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
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|==
literal|false
condition|)
return|return
literal|false
return|;
name|AbstractPrefixTreeFilter
name|that
init|=
operator|(
name|AbstractPrefixTreeFilter
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|detailLevel
operator|!=
name|that
operator|.
name|detailLevel
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|fieldName
operator|.
name|equals
argument_list|(
name|that
operator|.
name|fieldName
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|queryShape
operator|.
name|equals
argument_list|(
name|that
operator|.
name|queryShape
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
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|queryShape
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|fieldName
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|detailLevel
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Holds transient state and docid collecting utility methods as part of    * traversing a {@link TermsEnum} for a {@link org.apache.lucene.index.LeafReaderContext}. */
DECL|class|BaseTermsEnumTraverser
specifier|public
specifier|abstract
class|class
name|BaseTermsEnumTraverser
block|{
comment|//TODO rename to LeafTermsEnumTraverser ?
comment|//note: only 'fieldName' (accessed in constructor) keeps this from being a static inner class
DECL|field|context
specifier|protected
specifier|final
name|LeafReaderContext
name|context
decl_stmt|;
DECL|field|acceptDocs
specifier|protected
name|Bits
name|acceptDocs
decl_stmt|;
DECL|field|maxDoc
specifier|protected
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|termsEnum
specifier|protected
name|TermsEnum
name|termsEnum
decl_stmt|;
comment|//remember to check for null!
DECL|field|postingsEnum
specifier|protected
name|PostingsEnum
name|postingsEnum
decl_stmt|;
DECL|method|BaseTermsEnumTraverser
specifier|public
name|BaseTermsEnumTraverser
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|this
operator|.
name|acceptDocs
operator|=
name|acceptDocs
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
name|Terms
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
name|this
operator|.
name|termsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
DECL|method|collectDocs
specifier|protected
name|void
name|collectDocs
parameter_list|(
name|BitSet
name|bitSet
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|termsEnum
operator|!=
literal|null
assert|;
name|postingsEnum
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|postingsEnum
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|bitSet
operator|.
name|or
argument_list|(
name|wrap
argument_list|(
name|postingsEnum
argument_list|,
name|acceptDocs
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|collectDocs
specifier|protected
name|void
name|collectDocs
parameter_list|(
name|DocIdSetBuilder
name|docSetBuilder
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|termsEnum
operator|!=
literal|null
assert|;
name|postingsEnum
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|postingsEnum
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|docSetBuilder
operator|.
name|add
argument_list|(
name|wrap
argument_list|(
name|postingsEnum
argument_list|,
name|acceptDocs
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Filter the given {@link PostingsEnum} with the given {@link Bits}. */
DECL|method|wrap
specifier|private
specifier|static
name|PostingsEnum
name|wrap
parameter_list|(
name|PostingsEnum
name|iterator
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
block|{
if|if
condition|(
name|iterator
operator|==
literal|null
operator|||
name|acceptDocs
operator|==
literal|null
condition|)
block|{
return|return
name|iterator
return|;
block|}
return|return
operator|new
name|BitsFilteredPostingsEnum
argument_list|(
name|iterator
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
comment|/** A {@link PostingsEnum} which is filtered by some random-access bits. */
DECL|class|BitsFilteredPostingsEnum
specifier|private
specifier|static
class|class
name|BitsFilteredPostingsEnum
extends|extends
name|FilterPostingsEnum
block|{
DECL|field|bits
specifier|private
specifier|final
name|Bits
name|bits
decl_stmt|;
DECL|method|BitsFilteredPostingsEnum
specifier|private
name|BitsFilteredPostingsEnum
parameter_list|(
name|PostingsEnum
name|in
parameter_list|,
name|Bits
name|bits
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|bits
operator|=
name|bits
expr_stmt|;
block|}
DECL|method|doNext
specifier|private
name|int
name|doNext
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|doc
operator|!=
name|NO_MORE_DOCS
operator|&&
name|bits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|==
literal|false
condition|)
block|{
name|doc
operator|=
name|in
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
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
return|return
name|doNext
argument_list|(
name|in
operator|.
name|nextDoc
argument_list|()
argument_list|)
return|;
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
return|return
name|doNext
argument_list|(
name|in
operator|.
name|advance
argument_list|(
name|target
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

