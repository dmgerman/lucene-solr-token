begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|FilterTerms
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
name|FilterTermsEnum
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
name|CompiledAutomaton
import|;
end_import

begin_comment
comment|/**  * The {@link ExitableDirectoryReader} wraps a real index {@link DirectoryReader} and  * allows for a {@link QueryTimeout} implementation object to be checked periodically  * to see if the thread should exit or not.  If {@link QueryTimeout#shouldExit()}  * returns true, an {@link ExitingReaderException} is thrown.  */
end_comment

begin_class
DECL|class|ExitableDirectoryReader
specifier|public
class|class
name|ExitableDirectoryReader
extends|extends
name|FilterDirectoryReader
block|{
DECL|field|queryTimeout
specifier|private
name|QueryTimeout
name|queryTimeout
decl_stmt|;
comment|/**    * Exception that is thrown to prematurely terminate a term enumeration.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|ExitingReaderException
specifier|public
specifier|static
class|class
name|ExitingReaderException
extends|extends
name|RuntimeException
block|{
comment|/** Constructor **/
DECL|method|ExitingReaderException
name|ExitingReaderException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Wrapper class for a SubReaderWrapper that is used by the ExitableDirectoryReader.    */
DECL|class|ExitableSubReaderWrapper
specifier|public
specifier|static
class|class
name|ExitableSubReaderWrapper
extends|extends
name|SubReaderWrapper
block|{
DECL|field|queryTimeout
specifier|private
name|QueryTimeout
name|queryTimeout
decl_stmt|;
comment|/** Constructor **/
DECL|method|ExitableSubReaderWrapper
specifier|public
name|ExitableSubReaderWrapper
parameter_list|(
name|QueryTimeout
name|queryTimeout
parameter_list|)
block|{
name|this
operator|.
name|queryTimeout
operator|=
name|queryTimeout
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|wrap
specifier|public
name|LeafReader
name|wrap
parameter_list|(
name|LeafReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|ExitableFilterAtomicReader
argument_list|(
name|reader
argument_list|,
name|queryTimeout
argument_list|)
return|;
block|}
block|}
comment|/**    * Wrapper class for another FilterAtomicReader. This is used by ExitableSubReaderWrapper.    */
DECL|class|ExitableFilterAtomicReader
specifier|public
specifier|static
class|class
name|ExitableFilterAtomicReader
extends|extends
name|FilterLeafReader
block|{
DECL|field|queryTimeout
specifier|private
name|QueryTimeout
name|queryTimeout
decl_stmt|;
comment|/** Constructor **/
DECL|method|ExitableFilterAtomicReader
specifier|public
name|ExitableFilterAtomicReader
parameter_list|(
name|LeafReader
name|in
parameter_list|,
name|QueryTimeout
name|queryTimeout
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryTimeout
operator|=
name|queryTimeout
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|Terms
name|terms
init|=
name|in
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|(
name|queryTimeout
operator|.
name|isTimeoutEnabled
argument_list|()
operator|)
condition|?
operator|new
name|ExitableTerms
argument_list|(
name|terms
argument_list|,
name|queryTimeout
argument_list|)
else|:
name|terms
return|;
block|}
comment|// this impl does not change deletes or data so we can delegate the
comment|// CacheHelpers
annotation|@
name|Override
DECL|method|getReaderCacheHelper
specifier|public
name|CacheHelper
name|getReaderCacheHelper
parameter_list|()
block|{
return|return
name|in
operator|.
name|getReaderCacheHelper
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCoreCacheHelper
specifier|public
name|CacheHelper
name|getCoreCacheHelper
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCoreCacheHelper
argument_list|()
return|;
block|}
block|}
comment|/**    * Wrapper class for another Terms implementation that is used by ExitableFields.    */
DECL|class|ExitableTerms
specifier|public
specifier|static
class|class
name|ExitableTerms
extends|extends
name|FilterTerms
block|{
DECL|field|queryTimeout
specifier|private
name|QueryTimeout
name|queryTimeout
decl_stmt|;
comment|/** Constructor **/
DECL|method|ExitableTerms
specifier|public
name|ExitableTerms
parameter_list|(
name|Terms
name|terms
parameter_list|,
name|QueryTimeout
name|queryTimeout
parameter_list|)
block|{
name|super
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryTimeout
operator|=
name|queryTimeout
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|intersect
specifier|public
name|TermsEnum
name|intersect
parameter_list|(
name|CompiledAutomaton
name|compiled
parameter_list|,
name|BytesRef
name|startTerm
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ExitableTermsEnum
argument_list|(
name|in
operator|.
name|intersect
argument_list|(
name|compiled
argument_list|,
name|startTerm
argument_list|)
argument_list|,
name|queryTimeout
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|TermsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|ExitableTermsEnum
argument_list|(
name|in
operator|.
name|iterator
argument_list|()
argument_list|,
name|queryTimeout
argument_list|)
return|;
block|}
block|}
comment|/**    * Wrapper class for TermsEnum that is used by ExitableTerms for implementing an    * exitable enumeration of terms.    */
DECL|class|ExitableTermsEnum
specifier|public
specifier|static
class|class
name|ExitableTermsEnum
extends|extends
name|FilterTermsEnum
block|{
DECL|field|queryTimeout
specifier|private
name|QueryTimeout
name|queryTimeout
decl_stmt|;
comment|/** Constructor **/
DECL|method|ExitableTermsEnum
specifier|public
name|ExitableTermsEnum
parameter_list|(
name|TermsEnum
name|termsEnum
parameter_list|,
name|QueryTimeout
name|queryTimeout
parameter_list|)
block|{
name|super
argument_list|(
name|termsEnum
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryTimeout
operator|=
name|queryTimeout
expr_stmt|;
name|checkAndThrow
argument_list|()
expr_stmt|;
block|}
comment|/**      * Throws {@link ExitingReaderException} if {@link QueryTimeout#shouldExit()} returns true,      * or if {@link Thread#interrupted()} returns true.      */
DECL|method|checkAndThrow
specifier|private
name|void
name|checkAndThrow
parameter_list|()
block|{
if|if
condition|(
name|queryTimeout
operator|.
name|shouldExit
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ExitingReaderException
argument_list|(
literal|"The request took too long to iterate over terms. Timeout: "
operator|+
name|queryTimeout
operator|.
name|toString
argument_list|()
operator|+
literal|", TermsEnum="
operator|+
name|in
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|Thread
operator|.
name|interrupted
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ExitingReaderException
argument_list|(
literal|"Interrupted while iterating over terms. TermsEnum="
operator|+
name|in
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Before every iteration, check if the iteration should exit
name|checkAndThrow
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|next
argument_list|()
return|;
block|}
block|}
comment|/**    * Constructor    * @param in DirectoryReader that this ExitableDirectoryReader wraps around to make it Exitable.    * @param queryTimeout The object to periodically check if the query should time out.    */
DECL|method|ExitableDirectoryReader
specifier|public
name|ExitableDirectoryReader
parameter_list|(
name|DirectoryReader
name|in
parameter_list|,
name|QueryTimeout
name|queryTimeout
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|,
operator|new
name|ExitableSubReaderWrapper
argument_list|(
name|queryTimeout
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryTimeout
operator|=
name|queryTimeout
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWrapDirectoryReader
specifier|protected
name|DirectoryReader
name|doWrapDirectoryReader
parameter_list|(
name|DirectoryReader
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ExitableDirectoryReader
argument_list|(
name|in
argument_list|,
name|queryTimeout
argument_list|)
return|;
block|}
comment|/**    * Wraps a provided DirectoryReader. Note that for convenience, the returned reader    * can be used normally (e.g. passed to {@link DirectoryReader#openIfChanged(DirectoryReader)})    * and so on.    */
DECL|method|wrap
specifier|public
specifier|static
name|DirectoryReader
name|wrap
parameter_list|(
name|DirectoryReader
name|in
parameter_list|,
name|QueryTimeout
name|queryTimeout
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ExitableDirectoryReader
argument_list|(
name|in
argument_list|,
name|queryTimeout
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getReaderCacheHelper
specifier|public
name|CacheHelper
name|getReaderCacheHelper
parameter_list|()
block|{
return|return
name|in
operator|.
name|getReaderCacheHelper
argument_list|()
return|;
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
literal|"ExitableDirectoryReader("
operator|+
name|in
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

