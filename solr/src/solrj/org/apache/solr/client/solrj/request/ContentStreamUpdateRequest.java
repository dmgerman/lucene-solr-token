begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.client.solrj.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|request
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|ContentStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|ContentStreamBase
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_comment
comment|/**  * Basic functionality to upload a File or {@link org.apache.solr.common.util.ContentStream} to a Solr Cell or some  * other handler that takes ContentStreams (CSV)  *<p/>  * See http://wiki.apache.org/solr/ExtractingRequestHandler<br/>  * See http://wiki.apache.org/solr/UpdateCSV  *   *  **/
end_comment

begin_class
DECL|class|ContentStreamUpdateRequest
specifier|public
class|class
name|ContentStreamUpdateRequest
extends|extends
name|AbstractUpdateRequest
block|{
DECL|field|contentStreams
name|List
argument_list|<
name|ContentStream
argument_list|>
name|contentStreams
decl_stmt|;
comment|/**    *    * @param url The URL to send the {@link org.apache.solr.common.util.ContentStream} to in Solr.    */
DECL|method|ContentStreamUpdateRequest
specifier|public
name|ContentStreamUpdateRequest
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|POST
argument_list|,
name|url
argument_list|)
expr_stmt|;
name|contentStreams
operator|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContentStreams
specifier|public
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|contentStreams
return|;
block|}
comment|/**    * Add a File to the {@link org.apache.solr.common.util.ContentStream}s.    * @param file The File to add.    * @throws IOException if there was an error with the file.    *    * @see #getContentStreams()    * @see org.apache.solr.common.util.ContentStreamBase.FileStream    */
DECL|method|addFile
specifier|public
name|void
name|addFile
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|addContentStream
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|FileStream
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a {@link org.apache.solr.common.util.ContentStream} to {@link #getContentStreams()}    * @param contentStream The {@link org.apache.solr.common.util.ContentStream}    */
DECL|method|addContentStream
specifier|public
name|void
name|addContentStream
parameter_list|(
name|ContentStream
name|contentStream
parameter_list|)
block|{
name|contentStreams
operator|.
name|add
argument_list|(
name|contentStream
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

