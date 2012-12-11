begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|DriverManager
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
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|derby
operator|.
name|iapi
operator|.
name|error
operator|.
name|StandardException
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
name|request
operator|.
name|LocalSolrQueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_comment
comment|/**  * This sets up an in-memory Sql database with a little sample data.  */
end_comment

begin_class
DECL|class|AbstractDIHJdbcTestCase
specifier|public
specifier|abstract
class|class
name|AbstractDIHJdbcTestCase
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
DECL|field|dbToUse
specifier|protected
name|Database
name|dbToUse
decl_stmt|;
DECL|enum|Database
specifier|public
enum|enum
name|Database
block|{
DECL|enum constant|RANDOM
DECL|enum constant|DERBY
DECL|enum constant|HSQLDB
name|RANDOM
block|,
name|DERBY
block|,
name|HSQLDB
block|}
DECL|field|skipThisTest
specifier|protected
name|boolean
name|skipThisTest
init|=
literal|false
decl_stmt|;
DECL|field|totalRequestsPattern
specifier|private
specifier|static
specifier|final
name|Pattern
name|totalRequestsPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".str name..Total Requests made to DataSource..(\\d+)..str."
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClassDihJdbcTest
specifier|public
specifier|static
name|void
name|beforeClassDihJdbcTest
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"org.hsqldb.jdbcDriver"
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|String
name|oldProp
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"derby.stream.error.field"
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"derby.stream.error.field"
argument_list|,
literal|"org.apache.solr.handler.dataimport.AbstractDIHJdbcTestCase$DerbyUtil.DEV_NULL"
argument_list|)
expr_stmt|;
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.derby.jdbc.EmbeddedDriver"
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
if|if
condition|(
name|oldProp
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"derby.stream.error.field"
argument_list|,
name|oldProp
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
name|initCore
argument_list|(
literal|"dataimport-solrconfig.xml"
argument_list|,
literal|"dataimport-schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClassDihJdbcTest
specifier|public
specifier|static
name|void
name|afterClassDihJdbcTest
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:derby:;shutdown=true"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// ignore...we might not even be using derby this time...
block|}
block|}
DECL|method|setAllowedDatabases
specifier|protected
name|Database
name|setAllowedDatabases
parameter_list|()
block|{
return|return
name|Database
operator|.
name|RANDOM
return|;
block|}
annotation|@
name|Before
DECL|method|beforeDihJdbcTest
specifier|public
name|void
name|beforeDihJdbcTest
parameter_list|()
throws|throws
name|Exception
block|{
name|skipThisTest
operator|=
literal|false
expr_stmt|;
name|dbToUse
operator|=
name|setAllowedDatabases
argument_list|()
expr_stmt|;
if|if
condition|(
name|dbToUse
operator|==
name|Database
operator|.
name|RANDOM
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|dbToUse
operator|=
name|Database
operator|.
name|DERBY
expr_stmt|;
block|}
else|else
block|{
name|dbToUse
operator|=
name|Database
operator|.
name|HSQLDB
expr_stmt|;
block|}
block|}
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|buildDatabase
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|afterDihJdbcTest
specifier|public
name|void
name|afterDihJdbcTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|conn
init|=
literal|null
decl_stmt|;
name|Statement
name|s
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|dbToUse
operator|==
name|Database
operator|.
name|DERBY
condition|)
block|{
try|try
block|{
name|conn
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:derby:memory:derbyDB;drop=true"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
literal|"08006"
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getSQLState
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|dbToUse
operator|==
name|Database
operator|.
name|HSQLDB
condition|)
block|{
name|conn
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:hsqldb:mem:."
argument_list|)
expr_stmt|;
name|s
operator|=
name|conn
operator|.
name|createStatement
argument_list|()
expr_stmt|;
name|s
operator|.
name|executeUpdate
argument_list|(
literal|"shutdown"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|skipThisTest
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
finally|finally
block|{
try|try
block|{
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
try|try
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
block|}
block|}
DECL|method|newConnection
specifier|protected
name|Connection
name|newConnection
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|dbToUse
operator|==
name|Database
operator|.
name|DERBY
condition|)
block|{
return|return
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:derby:memory:derbyDB;"
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|dbToUse
operator|==
name|Database
operator|.
name|HSQLDB
condition|)
block|{
return|return
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:hsqldb:mem:."
argument_list|)
return|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Invalid database to use: "
operator|+
name|dbToUse
argument_list|)
throw|;
block|}
DECL|method|buildDatabase
specifier|protected
name|void
name|buildDatabase
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|conn
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|dbToUse
operator|==
name|Database
operator|.
name|DERBY
condition|)
block|{
name|conn
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:derby:memory:derbyDB;create=true"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dbToUse
operator|==
name|Database
operator|.
name|HSQLDB
condition|)
block|{
name|conn
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:hsqldb:mem:."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Invalid database to use: "
operator|+
name|dbToUse
argument_list|)
throw|;
block|}
name|populateData
argument_list|(
name|conn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|sqe
parameter_list|)
block|{
name|Throwable
name|cause
init|=
name|sqe
decl_stmt|;
while|while
condition|(
name|cause
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|cause
operator|=
name|cause
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
name|String
name|message
init|=
name|cause
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|instanceof
name|StandardException
condition|)
block|{
name|message
operator|=
operator|(
operator|(
name|StandardException
operator|)
name|cause
operator|)
operator|.
name|getMessageId
argument_list|()
expr_stmt|;
block|}
comment|//Derby INVALID_LOCALE_DESCRIPTION
if|if
condition|(
literal|"XBM0X.D"
operator|.
name|equals
argument_list|(
name|message
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Skipping test because Database "
operator|+
name|dbToUse
operator|+
literal|" does not support the locale "
operator|+
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
name|skipThisTest
operator|=
literal|true
expr_stmt|;
name|Assume
operator|.
name|assumeNoException
argument_list|(
name|sqe
argument_list|)
expr_stmt|;
throw|throw
name|sqe
throw|;
block|}
block|}
finally|finally
block|{
try|try
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{}
block|}
block|}
DECL|method|populateData
specifier|protected
name|void
name|populateData
parameter_list|(
name|Connection
name|conn
parameter_list|)
throws|throws
name|Exception
block|{
comment|// no-op
block|}
DECL|method|totalDatabaseRequests
specifier|public
name|int
name|totalDatabaseRequests
parameter_list|(
name|String
name|dihHandlerName
parameter_list|)
throws|throws
name|Exception
block|{
name|LocalSolrQueryRequest
name|request
init|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
decl_stmt|;
name|String
name|response
init|=
name|h
operator|.
name|query
argument_list|(
name|dihHandlerName
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|totalRequestsPattern
operator|.
name|matcher
argument_list|(
name|response
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The handler "
operator|+
name|dihHandlerName
operator|+
literal|" is not reporting any database requests. "
argument_list|,
name|m
operator|.
name|find
argument_list|()
operator|&&
name|m
operator|.
name|groupCount
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
block|}
DECL|method|totalDatabaseRequests
specifier|public
name|int
name|totalDatabaseRequests
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|totalDatabaseRequests
argument_list|(
literal|"/dataimport"
argument_list|)
return|;
block|}
DECL|method|generateRequest
specifier|protected
name|LocalSolrQueryRequest
name|generateRequest
parameter_list|()
block|{
return|return
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|,
literal|"dataConfig"
argument_list|,
name|generateConfig
argument_list|()
argument_list|,
literal|"clean"
argument_list|,
literal|"true"
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"synchronous"
argument_list|,
literal|"true"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
return|;
block|}
DECL|method|generateConfig
specifier|protected
specifier|abstract
name|String
name|generateConfig
parameter_list|()
function_decl|;
DECL|class|DerbyUtil
specifier|public
specifier|static
class|class
name|DerbyUtil
block|{
DECL|field|DEV_NULL
specifier|public
specifier|static
specifier|final
name|OutputStream
name|DEV_NULL
init|=
operator|new
name|OutputStream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
block|{}
block|}
decl_stmt|;
block|}
block|}
end_class

end_unit

