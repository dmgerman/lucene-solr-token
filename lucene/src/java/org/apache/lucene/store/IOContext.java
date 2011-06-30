begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * IOContext holds additional details on the merge/search context. A IOContext  * object can never be initialized as null as passed as a parameter to either  * {@link #org.apache.lucene.store.Directory.openInput()} or  * {@link #org.apache.lucene.store.Directory.createInput()}  */
end_comment

begin_class
DECL|class|IOContext
specifier|public
class|class
name|IOContext
block|{
comment|/**    * Context is a enumerator which specifies the context in which the Directory    * is being used for.    */
DECL|enum|Context
specifier|public
enum|enum
name|Context
block|{
DECL|enum constant|MERGE
DECL|enum constant|READ
DECL|enum constant|FLUSH
DECL|enum constant|DEFAULT
name|MERGE
block|,
name|READ
block|,
name|FLUSH
block|,
name|DEFAULT
block|}
empty_stmt|;
comment|/**    * An object of a enumerator Context type    */
DECL|field|context
specifier|public
specifier|final
name|Context
name|context
decl_stmt|;
DECL|field|mergeInfo
specifier|public
specifier|final
name|MergeInfo
name|mergeInfo
decl_stmt|;
DECL|field|flushInfo
specifier|public
specifier|final
name|FlushInfo
name|flushInfo
decl_stmt|;
DECL|field|readOnce
specifier|public
specifier|final
name|boolean
name|readOnce
decl_stmt|;
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|IOContext
name|DEFAULT
init|=
operator|new
name|IOContext
argument_list|(
name|Context
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
DECL|field|READONCE
specifier|public
specifier|static
specifier|final
name|IOContext
name|READONCE
init|=
operator|new
name|IOContext
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|READ
specifier|public
specifier|static
specifier|final
name|IOContext
name|READ
init|=
operator|new
name|IOContext
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|method|IOContext
specifier|public
name|IOContext
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|IOContext
specifier|public
name|IOContext
parameter_list|(
name|FlushInfo
name|flushInfo
parameter_list|)
block|{
assert|assert
name|flushInfo
operator|!=
literal|null
assert|;
name|this
operator|.
name|context
operator|=
name|Context
operator|.
name|FLUSH
expr_stmt|;
name|this
operator|.
name|mergeInfo
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|readOnce
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|flushInfo
operator|=
name|flushInfo
expr_stmt|;
block|}
DECL|method|IOContext
specifier|public
name|IOContext
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|IOContext
specifier|private
name|IOContext
parameter_list|(
name|boolean
name|readOnce
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|Context
operator|.
name|READ
expr_stmt|;
name|this
operator|.
name|mergeInfo
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|readOnce
operator|=
name|readOnce
expr_stmt|;
name|this
operator|.
name|flushInfo
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|IOContext
specifier|public
name|IOContext
parameter_list|(
name|MergeInfo
name|mergeInfo
parameter_list|)
block|{
name|this
argument_list|(
name|Context
operator|.
name|MERGE
argument_list|,
name|mergeInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|IOContext
specifier|private
name|IOContext
parameter_list|(
name|Context
name|context
parameter_list|,
name|MergeInfo
name|mergeInfo
parameter_list|)
block|{
assert|assert
name|context
operator|!=
name|Context
operator|.
name|MERGE
operator|||
name|mergeInfo
operator|!=
literal|null
operator|:
literal|"MergeInfo must not be null if context is MERGE"
assert|;
assert|assert
name|context
operator|!=
name|Context
operator|.
name|FLUSH
operator|:
literal|"Use IOContext(FlushInfo) to create a FLUSH IOContext"
assert|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|readOnce
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|mergeInfo
operator|=
name|mergeInfo
expr_stmt|;
name|this
operator|.
name|flushInfo
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

