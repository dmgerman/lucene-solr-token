begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|ReaderUtil
import|;
end_import

begin_comment
comment|// javadoc
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
name|DirectoryReader
import|;
end_import

begin_comment
comment|// javadoc
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
name|MultiReader
import|;
end_import

begin_comment
comment|// javadoc
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
name|codecs
operator|.
name|PerDocValues
import|;
end_import

begin_comment
comment|/**  * This class forces a composite reader (eg a {@link  * MultiReader} or {@link DirectoryReader} or any other  * IndexReader subclass that returns non-null from {@link  * IndexReader#getSequentialSubReaders}) to emulate an  * atomic reader.  This requires implementing the postings  * APIs on-the-fly, using the static methods in {@link  * MultiFields}, by stepping through the sub-readers to  * merge fields/terms, appending docs, etc.  *  *<p>If you ever hit an UnsupportedOperationException saying  * "please use MultiFields.XXX instead", the simple  * but non-performant workaround is to wrap your reader  * using this class.</p>  *  *<p><b>NOTE</b>: this class almost always results in a  * performance hit.  If this is important to your use case,  * it's better to get the sequential sub readers (see {@link  * ReaderUtil#gatherSubReaders}, instead, and iterate through them  * yourself.</p>  */
end_comment

begin_class
DECL|class|SlowMultiReaderWrapper
specifier|public
specifier|final
class|class
name|SlowMultiReaderWrapper
extends|extends
name|FilterIndexReader
block|{
DECL|field|readerContext
specifier|private
specifier|final
name|ReaderContext
name|readerContext
decl_stmt|;
DECL|field|normsCache
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|normsCache
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|SlowMultiReaderWrapper
specifier|public
name|SlowMultiReaderWrapper
parameter_list|(
name|IndexReader
name|other
parameter_list|)
block|{
name|super
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|readerContext
operator|=
operator|new
name|AtomicReaderContext
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// emulate atomic reader!
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SlowMultiReaderWrapper("
operator|+
name|in
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Fields
name|fields
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiFields
operator|.
name|getFields
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|perDocValues
specifier|public
name|PerDocValues
name|perDocValues
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiPerDocValues
operator|.
name|getPerDocs
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getLiveDocs
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSequentialSubReaders
specifier|public
name|IndexReader
index|[]
name|getSequentialSubReaders
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|norms
specifier|public
specifier|synchronized
name|byte
index|[]
name|norms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|normsCache
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
return|return
name|bytes
return|;
if|if
condition|(
operator|!
name|hasNorms
argument_list|(
name|field
argument_list|)
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|normsCache
operator|.
name|containsKey
argument_list|(
name|field
argument_list|)
condition|)
comment|// cached omitNorms, not missing key
return|return
literal|null
return|;
name|bytes
operator|=
name|MultiNorms
operator|.
name|norms
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|normsCache
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|getTopReaderContext
specifier|public
name|ReaderContext
name|getTopReaderContext
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|readerContext
return|;
block|}
block|}
end_class

end_unit

