begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.server
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
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
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|gdata
operator|.
name|data
operator|.
name|ServerBaseEntry
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
name|gdata
operator|.
name|data
operator|.
name|ServerBaseFeed
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
name|gdata
operator|.
name|search
operator|.
name|GDataSearcher
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
name|gdata
operator|.
name|search
operator|.
name|SearchComponent
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
name|gdata
operator|.
name|search
operator|.
name|query
operator|.
name|GDataQueryParser
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|ComponentType
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|GDataServerRegistry
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|ProvidedService
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
name|gdata
operator|.
name|storage
operator|.
name|StorageException
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
name|queryParser
operator|.
name|ParseException
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
name|queryParser
operator|.
name|QueryParser
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
name|Query
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseEntry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseFeed
import|;
end_import

begin_comment
comment|/**  * @author Simon Willnauer  *  */
end_comment

begin_class
DECL|class|GDataSearchService
specifier|public
class|class
name|GDataSearchService
extends|extends
name|GDataService
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|GDataSearchService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SEARCHCOMPONENT
specifier|private
specifier|static
name|SearchComponent
name|SEARCHCOMPONENT
decl_stmt|;
DECL|field|searcher
specifier|private
name|GDataSearcher
argument_list|<
name|String
argument_list|>
name|searcher
decl_stmt|;
DECL|method|GDataSearchService
specifier|protected
name|GDataSearchService
parameter_list|()
throws|throws
name|ServiceException
block|{
if|if
condition|(
name|SEARCHCOMPONENT
operator|==
literal|null
condition|)
name|SEARCHCOMPONENT
operator|=
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|lookup
argument_list|(
name|SearchComponent
operator|.
name|class
argument_list|,
name|ComponentType
operator|.
name|SEARCHCONTROLLER
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.apache.lucene.gdata.server.GDataService#getFeed(org.apache.lucene.gdata.server.GDataRequest, org.apache.lucene.gdata.server.GDataResponse)      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|getFeed
specifier|public
name|BaseFeed
name|getFeed
parameter_list|(
name|GDataRequest
name|request
parameter_list|,
name|GDataResponse
name|response
parameter_list|)
throws|throws
name|ServiceException
block|{
name|String
name|translatedQuery
init|=
name|request
operator|.
name|getTranslatedQuery
argument_list|()
decl_stmt|;
name|ProvidedService
name|service
init|=
name|request
operator|.
name|getConfigurator
argument_list|()
decl_stmt|;
name|QueryParser
name|parser
init|=
operator|new
name|GDataQueryParser
argument_list|(
name|service
operator|.
name|getIndexSchema
argument_list|()
argument_list|)
decl_stmt|;
name|Query
name|query
decl_stmt|;
try|try
block|{
name|query
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|translatedQuery
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Search Failed -- Can not parse query"
argument_list|,
name|e1
argument_list|,
name|GDataResponse
operator|.
name|BAD_REQUEST
argument_list|)
throw|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Fire search for user query  query: "
operator|+
name|query
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
name|SEARCHCOMPONENT
operator|.
name|getServiceSearcher
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|result
decl_stmt|;
try|try
block|{
name|result
operator|=
name|this
operator|.
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|request
operator|.
name|getItemsPerPage
argument_list|()
argument_list|,
name|request
operator|.
name|getStartIndex
argument_list|()
argument_list|,
name|request
operator|.
name|getFeedId
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Search Failed -- Searcher throws IOException"
argument_list|,
name|e
argument_list|,
name|GDataResponse
operator|.
name|SERVER_ERROR
argument_list|)
throw|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Fetching results for user query result size: "
operator|+
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ServerBaseFeed
name|requestFeed
init|=
operator|new
name|ServerBaseFeed
argument_list|()
decl_stmt|;
name|requestFeed
operator|.
name|setServiceConfig
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|requestFeed
operator|.
name|setStartIndex
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|requestFeed
operator|.
name|setItemsPerPage
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|requestFeed
operator|.
name|setId
argument_list|(
name|request
operator|.
name|getFeedId
argument_list|()
argument_list|)
expr_stmt|;
name|BaseFeed
name|feed
init|=
literal|null
decl_stmt|;
try|try
block|{
name|feed
operator|=
name|this
operator|.
name|storage
operator|.
name|getFeed
argument_list|(
name|requestFeed
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Search Failed -- can not get feed, feed not stored "
argument_list|,
name|e
argument_list|,
name|GDataResponse
operator|.
name|NOT_FOUND
argument_list|)
throw|;
block|}
for|for
control|(
name|String
name|entryId
range|:
name|result
control|)
block|{
name|ServerBaseEntry
name|requestEntry
init|=
operator|new
name|ServerBaseEntry
argument_list|()
decl_stmt|;
name|requestEntry
operator|.
name|setId
argument_list|(
name|entryId
argument_list|)
expr_stmt|;
name|requestEntry
operator|.
name|setServiceConfig
argument_list|(
name|service
argument_list|)
expr_stmt|;
try|try
block|{
name|BaseEntry
name|entry
init|=
name|this
operator|.
name|storage
operator|.
name|getEntry
argument_list|(
name|requestEntry
argument_list|)
decl_stmt|;
name|feed
operator|.
name|getEntries
argument_list|()
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"StorageException caught while fetching query results -- skip entry -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|dynamicElementFeedStragey
argument_list|(
name|feed
argument_list|,
name|request
argument_list|)
expr_stmt|;
return|return
name|feed
return|;
block|}
block|}
end_class

end_unit

