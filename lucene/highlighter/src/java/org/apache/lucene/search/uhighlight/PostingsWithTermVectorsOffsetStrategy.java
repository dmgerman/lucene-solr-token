begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.uhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|uhighlight
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
name|Collections
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
name|ReaderUtil
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
name|automaton
operator|.
name|CharacterRunAutomaton
import|;
end_import

begin_comment
comment|/**  * Like {@link PostingsOffsetStrategy} but also uses term vectors (only terms needed) for multi-term queries.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|PostingsWithTermVectorsOffsetStrategy
specifier|public
class|class
name|PostingsWithTermVectorsOffsetStrategy
extends|extends
name|FieldOffsetStrategy
block|{
DECL|method|PostingsWithTermVectorsOffsetStrategy
specifier|public
name|PostingsWithTermVectorsOffsetStrategy
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
index|[]
name|queryTerms
parameter_list|,
name|PhraseHelper
name|phraseHelper
parameter_list|,
name|CharacterRunAutomaton
index|[]
name|automata
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|queryTerms
argument_list|,
name|phraseHelper
argument_list|,
name|automata
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOffsetsEnums
specifier|public
name|List
argument_list|<
name|OffsetsEnum
argument_list|>
name|getOffsetsEnums
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|IOException
block|{
name|LeafReader
name|leafReader
decl_stmt|;
if|if
condition|(
name|reader
operator|instanceof
name|LeafReader
condition|)
block|{
name|leafReader
operator|=
operator|(
name|LeafReader
operator|)
name|reader
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|LeafReaderContext
name|LeafReaderContext
init|=
name|leaves
operator|.
name|get
argument_list|(
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|docId
argument_list|,
name|leaves
argument_list|)
argument_list|)
decl_stmt|;
name|leafReader
operator|=
name|LeafReaderContext
operator|.
name|reader
argument_list|()
expr_stmt|;
name|docId
operator|-=
name|LeafReaderContext
operator|.
name|docBase
expr_stmt|;
comment|// adjust 'doc' to be within this atomic reader
block|}
name|Terms
name|docTerms
init|=
name|leafReader
operator|.
name|getTermVector
argument_list|(
name|docId
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|docTerms
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|leafReader
operator|=
operator|new
name|TermVectorFilteredLeafReader
argument_list|(
name|leafReader
argument_list|,
name|docTerms
argument_list|)
expr_stmt|;
return|return
name|createOffsetsEnumsFromReader
argument_list|(
name|leafReader
argument_list|,
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getOffsetSource
specifier|public
name|UnifiedHighlighter
operator|.
name|OffsetSource
name|getOffsetSource
parameter_list|()
block|{
return|return
name|UnifiedHighlighter
operator|.
name|OffsetSource
operator|.
name|POSTINGS_WITH_TERM_VECTORS
return|;
block|}
block|}
end_class

end_unit

