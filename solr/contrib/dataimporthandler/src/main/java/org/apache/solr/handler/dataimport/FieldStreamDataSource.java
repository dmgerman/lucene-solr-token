begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Blob
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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|SEVERE
import|;
end_import

begin_comment
comment|/**  * This can be useful for users who have a DB field containing BLOBs which may be Rich documents  *<p/>  * The datasouce may be configured as follows  *<p/>  *<datasource name="f1" type="FieldStreamDataSource" />  *<p/>  * The enity which uses this datasource must keep and attribute dataField  *<p/>  * The fieldname must be resolvable from VariableResolver  *<p/>  * This may be used with any EntityProcessor which uses a DataSource<InputStream> eg:TikaEntityProcessor  *<p/>  *  * @version $Id$  * @since 1.5  */
end_comment

begin_class
DECL|class|FieldStreamDataSource
specifier|public
class|class
name|FieldStreamDataSource
extends|extends
name|DataSource
argument_list|<
name|InputStream
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FieldReaderDataSource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|vr
specifier|protected
name|VariableResolver
name|vr
decl_stmt|;
DECL|field|dataField
specifier|protected
name|String
name|dataField
decl_stmt|;
DECL|field|wrapper
specifier|private
name|EntityProcessorWrapper
name|wrapper
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|,
name|Properties
name|initProps
parameter_list|)
block|{
name|dataField
operator|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"dataField"
argument_list|)
expr_stmt|;
name|wrapper
operator|=
operator|(
name|EntityProcessorWrapper
operator|)
name|context
operator|.
name|getEntityProcessor
argument_list|()
expr_stmt|;
comment|/*no op*/
block|}
DECL|method|getData
specifier|public
name|InputStream
name|getData
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|Object
name|o
init|=
name|wrapper
operator|.
name|getVariableResolver
argument_list|()
operator|.
name|resolve
argument_list|(
name|dataField
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"No field available for name : "
operator|+
name|dataField
argument_list|)
throw|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|Blob
condition|)
block|{
name|Blob
name|blob
init|=
operator|(
name|Blob
operator|)
name|o
decl_stmt|;
try|try
block|{
comment|//Most of the JDBC drivers have getBinaryStream defined as public
comment|// so let us just check it
name|Method
name|m
init|=
name|blob
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredMethod
argument_list|(
literal|"getBinaryStream"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Modifier
operator|.
name|isPublic
argument_list|(
name|m
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|(
name|InputStream
operator|)
name|m
operator|.
name|invoke
argument_list|(
name|blob
argument_list|)
return|;
block|}
else|else
block|{
comment|// force invoke
name|m
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|(
name|InputStream
operator|)
name|m
operator|.
name|invoke
argument_list|(
name|blob
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Unable to get data from BLOB"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|byte
index|[]
condition|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|(
name|byte
index|[]
operator|)
name|o
decl_stmt|;
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unsupported type : "
operator|+
name|o
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{   }
block|}
end_class

end_unit

