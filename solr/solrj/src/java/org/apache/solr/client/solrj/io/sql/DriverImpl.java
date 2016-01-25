begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.client.solrj.io.sql
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
name|io
operator|.
name|sql
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Driver
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverPropertyInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|NameValuePair
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|utils
operator|.
name|URLEncodedUtils
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
name|SuppressForbidden
import|;
end_import

begin_comment
comment|/**  * Get a Connection with with a url and properties.  *  * jdbc:solr://zkhost:port?collection=collection&amp;aggregationMode=map_reduce  **/
end_comment

begin_class
DECL|class|DriverImpl
specifier|public
class|class
name|DriverImpl
implements|implements
name|Driver
block|{
static|static
block|{
try|try
block|{
name|DriverManager
operator|.
name|registerDriver
argument_list|(
operator|new
name|DriverImpl
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can't register driver!"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|connect
specifier|public
name|Connection
name|connect
parameter_list|(
name|String
name|url
parameter_list|,
name|Properties
name|props
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
operator|!
name|acceptsURL
argument_list|(
name|url
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|URI
name|uri
init|=
name|processUrl
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|loadParams
argument_list|(
name|uri
argument_list|,
name|props
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|props
operator|.
name|containsKey
argument_list|(
literal|"collection"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"The connection url has no connection properties. At a mininum the collection must be specified."
argument_list|)
throw|;
block|}
name|String
name|collection
init|=
operator|(
name|String
operator|)
name|props
operator|.
name|remove
argument_list|(
literal|"collection"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|props
operator|.
name|containsKey
argument_list|(
literal|"aggregationMode"
argument_list|)
condition|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
literal|"aggregationMode"
argument_list|,
literal|"facet"
argument_list|)
expr_stmt|;
block|}
comment|// JDBC requires metadata like field names from the SQLHandler. Force this property to be true.
name|props
operator|.
name|setProperty
argument_list|(
literal|"includeMetadata"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|String
name|zkHost
init|=
name|uri
operator|.
name|getAuthority
argument_list|()
operator|+
name|uri
operator|.
name|getPath
argument_list|()
decl_stmt|;
return|return
operator|new
name|ConnectionImpl
argument_list|(
name|url
argument_list|,
name|zkHost
argument_list|,
name|collection
argument_list|,
name|props
argument_list|)
return|;
block|}
DECL|method|connect
specifier|public
name|Connection
name|connect
parameter_list|(
name|String
name|url
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|connect
argument_list|(
name|url
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getMajorVersion
specifier|public
name|int
name|getMajorVersion
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
DECL|method|getMinorVersion
specifier|public
name|int
name|getMinorVersion
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|acceptsURL
specifier|public
name|boolean
name|acceptsURL
parameter_list|(
name|String
name|url
parameter_list|)
block|{
return|return
name|url
operator|!=
literal|null
operator|&&
name|url
operator|.
name|startsWith
argument_list|(
literal|"jdbc:solr"
argument_list|)
return|;
block|}
DECL|method|jdbcCompliant
specifier|public
name|boolean
name|jdbcCompliant
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Required by jdbc"
argument_list|)
DECL|method|getParentLogger
specifier|public
name|Logger
name|getParentLogger
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getPropertyInfo
specifier|public
name|DriverPropertyInfo
index|[]
name|getPropertyInfo
parameter_list|(
name|String
name|url
parameter_list|,
name|Properties
name|info
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|processUrl
specifier|protected
name|URI
name|processUrl
parameter_list|(
name|String
name|url
parameter_list|)
throws|throws
name|SQLException
block|{
name|URI
name|uri
decl_stmt|;
try|try
block|{
name|uri
operator|=
operator|new
name|URI
argument_list|(
name|url
operator|.
name|replaceFirst
argument_list|(
literal|"jdbc:"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|uri
operator|.
name|getAuthority
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"The zkHost must not be null"
argument_list|)
throw|;
block|}
return|return
name|uri
return|;
block|}
DECL|method|loadParams
specifier|private
name|void
name|loadParams
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Properties
name|props
parameter_list|)
throws|throws
name|SQLException
block|{
name|List
argument_list|<
name|NameValuePair
argument_list|>
name|parsedParams
init|=
name|URLEncodedUtils
operator|.
name|parse
argument_list|(
name|uri
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
for|for
control|(
name|NameValuePair
name|pair
range|:
name|parsedParams
control|)
block|{
if|if
condition|(
name|pair
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|props
operator|.
name|put
argument_list|(
name|pair
operator|.
name|getName
argument_list|()
argument_list|,
name|pair
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|props
operator|.
name|put
argument_list|(
name|pair
operator|.
name|getName
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

