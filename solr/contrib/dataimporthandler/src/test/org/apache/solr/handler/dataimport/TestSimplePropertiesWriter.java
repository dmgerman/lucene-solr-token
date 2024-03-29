begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|ResultSet
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
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|Locale
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
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SuppressForbidden
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|TestSimplePropertiesWriter
specifier|public
class|class
name|TestSimplePropertiesWriter
extends|extends
name|AbstractDIHJdbcTestCase
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|useJdbcEscapeSyntax
specifier|private
name|boolean
name|useJdbcEscapeSyntax
decl_stmt|;
DECL|field|dateFormat
specifier|private
name|String
name|dateFormat
decl_stmt|;
DECL|field|fileLocation
specifier|private
name|String
name|fileLocation
decl_stmt|;
DECL|field|fileName
specifier|private
name|String
name|fileName
decl_stmt|;
annotation|@
name|Before
DECL|method|spwBefore
specifier|public
name|void
name|spwBefore
parameter_list|()
throws|throws
name|Exception
block|{
name|fileLocation
operator|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|fileName
operator|=
literal|"the.properties"
expr_stmt|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Needs currentTimeMillis to construct date stamps"
argument_list|)
annotation|@
name|Test
DECL|method|testSimplePropertiesWriter
specifier|public
name|void
name|testSimplePropertiesWriter
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleDateFormat
name|errMsgFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm:ss.SSSSSS"
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|String
index|[]
name|d
init|=
block|{
literal|"{'ts' ''yyyy-MM-dd HH:mm:ss.SSSSSS''}"
block|,
literal|"{'ts' ''yyyy-MM-dd HH:mm:ss''}"
block|,
literal|"yyyy-MM-dd HH:mm:ss"
block|,
literal|"yyyy-MM-dd HH:mm:ss.SSSSSS"
block|}
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|d
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|<
literal|2
condition|)
block|{
name|useJdbcEscapeSyntax
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|useJdbcEscapeSyntax
operator|=
literal|false
expr_stmt|;
block|}
name|dateFormat
operator|=
name|d
index|[
name|i
index|]
expr_stmt|;
name|SimpleDateFormat
name|df
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|dateFormat
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|Date
name|oneSecondAgo
init|=
operator|new
name|Date
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
literal|1000
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|init
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|init
operator|.
name|put
argument_list|(
literal|"dateFormat"
argument_list|,
name|dateFormat
argument_list|)
expr_stmt|;
name|init
operator|.
name|put
argument_list|(
literal|"filename"
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|init
operator|.
name|put
argument_list|(
literal|"directory"
argument_list|,
name|fileLocation
argument_list|)
expr_stmt|;
name|SimplePropertiesWriter
name|spw
init|=
operator|new
name|SimplePropertiesWriter
argument_list|()
decl_stmt|;
name|spw
operator|.
name|init
argument_list|(
operator|new
name|DataImporter
argument_list|()
argument_list|,
name|init
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"SomeDates.last_index_time"
argument_list|,
name|oneSecondAgo
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"last_index_time"
argument_list|,
name|oneSecondAgo
argument_list|)
expr_stmt|;
name|spw
operator|.
name|persist
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|h
operator|.
name|query
argument_list|(
literal|"/dataimport"
argument_list|,
name|generateRequest
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|=
name|spw
operator|.
name|readIndexerProperties
argument_list|()
expr_stmt|;
name|Date
name|entityDate
init|=
name|df
operator|.
name|parse
argument_list|(
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"SomeDates.last_index_time"
argument_list|)
argument_list|)
decl_stmt|;
name|Date
name|docDate
init|=
name|df
operator|.
name|parse
argument_list|(
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"last_index_time"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|year
init|=
name|currentYearFromDatabase
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"This date: "
operator|+
name|errMsgFormat
operator|.
name|format
argument_list|(
name|oneSecondAgo
argument_list|)
operator|+
literal|" should be prior to the document date: "
operator|+
name|errMsgFormat
operator|.
name|format
argument_list|(
name|docDate
argument_list|)
argument_list|,
name|docDate
operator|.
name|getTime
argument_list|()
operator|-
name|oneSecondAgo
operator|.
name|getTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"This date: "
operator|+
name|errMsgFormat
operator|.
name|format
argument_list|(
name|oneSecondAgo
argument_list|)
operator|+
literal|" should be prior to the entity date: "
operator|+
name|errMsgFormat
operator|.
name|format
argument_list|(
name|entityDate
argument_list|)
argument_list|,
name|entityDate
operator|.
name|getTime
argument_list|()
operator|-
name|oneSecondAgo
operator|.
name|getTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//doc/str[@name=\"ayear_s\"]=\""
operator|+
name|year
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|currentYearFromDatabase
specifier|private
name|int
name|currentYearFromDatabase
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
name|ResultSet
name|rs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|conn
operator|=
name|newConnection
argument_list|()
expr_stmt|;
name|s
operator|=
name|conn
operator|.
name|createStatement
argument_list|()
expr_stmt|;
name|rs
operator|=
name|s
operator|.
name|executeQuery
argument_list|(
literal|"select year(current_timestamp) from sysibm.sysdummy1"
argument_list|)
expr_stmt|;
if|if
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
name|rs
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
return|;
block|}
name|Assert
operator|.
name|fail
argument_list|(
literal|"We should have gotten a row from the db."
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
name|e
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|rs
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
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|setAllowedDatabases
specifier|protected
name|Database
name|setAllowedDatabases
parameter_list|()
block|{
return|return
name|Database
operator|.
name|DERBY
return|;
block|}
annotation|@
name|Override
DECL|method|generateConfig
specifier|protected
name|String
name|generateConfig
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|q
init|=
name|useJdbcEscapeSyntax
condition|?
literal|""
else|:
literal|"'"
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<dataConfig> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<propertyWriter dateFormat=\""
operator|+
name|dateFormat
operator|+
literal|"\" type=\"SimplePropertiesWriter\" directory=\""
operator|+
name|fileLocation
operator|+
literal|"\" filename=\""
operator|+
name|fileName
operator|+
literal|"\" />\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<dataSource name=\"derby\" driver=\"org.apache.derby.jdbc.EmbeddedDriver\" url=\"jdbc:derby:memory:derbyDB;territory=en_US\" /> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<document name=\"TestSimplePropertiesWriter\"> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<entity name=\"SomeDates\" processor=\"SqlEntityProcessor\" dataSource=\"derby\" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"query=\"select 1 as id, YEAR("
operator|+
name|q
operator|+
literal|"${dih.last_index_time}"
operator|+
name|q
operator|+
literal|") as AYEAR_S from sysibm.sysdummy1 \">\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<field column=\"AYEAR_S\" name=\"ayear_s\" /> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</entity>\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</document> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</dataConfig> \n"
argument_list|)
expr_stmt|;
name|String
name|config
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|config
argument_list|)
expr_stmt|;
return|return
name|config
return|;
block|}
block|}
end_class

end_unit

