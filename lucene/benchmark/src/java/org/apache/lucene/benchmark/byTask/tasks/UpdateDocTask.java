begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|DocMaker
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
name|document
operator|.
name|Document
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
name|Term
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
name|IndexWriter
import|;
end_import

begin_comment
comment|/**  * Update a document, using IndexWriter.updateDocument,  * optionally with of a certain size.  *<br>Other side effects: none.  *<br>Takes optional param: document size.   */
end_comment

begin_class
DECL|class|UpdateDocTask
specifier|public
class|class
name|UpdateDocTask
extends|extends
name|PerfTask
block|{
DECL|method|UpdateDocTask
specifier|public
name|UpdateDocTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
DECL|field|docSize
specifier|private
name|int
name|docSize
init|=
literal|0
decl_stmt|;
comment|// volatile data passed between setup(), doLogic(), tearDown().
DECL|field|doc
specifier|private
name|Document
name|doc
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|DocMaker
name|docMaker
init|=
name|getRunData
argument_list|()
operator|.
name|getDocMaker
argument_list|()
decl_stmt|;
if|if
condition|(
name|docSize
operator|>
literal|0
condition|)
block|{
name|doc
operator|=
name|docMaker
operator|.
name|makeDocument
argument_list|(
name|docSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|=
name|docMaker
operator|.
name|makeDocument
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|doc
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|docID
init|=
name|doc
operator|.
name|get
argument_list|(
name|DocMaker
operator|.
name|ID_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|docID
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"document must define the docid field"
argument_list|)
throw|;
block|}
specifier|final
name|IndexWriter
name|iw
init|=
name|getRunData
argument_list|()
operator|.
name|getIndexWriter
argument_list|()
decl_stmt|;
name|iw
operator|.
name|updateDocument
argument_list|(
operator|new
name|Term
argument_list|(
name|DocMaker
operator|.
name|ID_FIELD
argument_list|,
name|docID
argument_list|)
argument_list|,
name|doc
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getLogMessage
specifier|protected
name|String
name|getLogMessage
parameter_list|(
name|int
name|recsCount
parameter_list|)
block|{
return|return
literal|"updated "
operator|+
name|recsCount
operator|+
literal|" docs"
return|;
block|}
comment|/**    * Set the params (docSize only)    * @param params docSize, or 0 for no limit.    */
annotation|@
name|Override
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
name|super
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|docSize
operator|=
operator|(
name|int
operator|)
name|Float
operator|.
name|parseFloat
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.tasks.PerfTask#supportsParams()    */
annotation|@
name|Override
DECL|method|supportsParams
specifier|public
name|boolean
name|supportsParams
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

