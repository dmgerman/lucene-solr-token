begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
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
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|FSDirectory
import|;
end_import

begin_comment
comment|/**  * Class containing some useful methods used by command line tools   *  */
end_comment

begin_class
DECL|class|CommandLineUtil
specifier|public
specifier|final
class|class
name|CommandLineUtil
block|{
DECL|method|CommandLineUtil
specifier|private
name|CommandLineUtil
parameter_list|()
block|{        }
comment|/**    * Creates a specific FSDirectory instance starting from its class name    * @param clazzName The name of the FSDirectory class to load    * @param path The path to be used as parameter constructor    * @return the new FSDirectory instance    */
DECL|method|newFSDirectory
specifier|public
specifier|static
name|FSDirectory
name|newFSDirectory
parameter_list|(
name|String
name|clazzName
parameter_list|,
name|Path
name|path
parameter_list|)
block|{
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|FSDirectory
argument_list|>
name|clazz
init|=
name|loadFSDirectoryClass
argument_list|(
name|clazzName
argument_list|)
decl_stmt|;
return|return
name|newFSDirectory
argument_list|(
name|clazz
argument_list|,
name|path
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|FSDirectory
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" implementation not found: "
operator|+
name|clazzName
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|clazzName
operator|+
literal|" is not a "
operator|+
name|FSDirectory
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" implementation"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|clazzName
operator|+
literal|" constructor with "
operator|+
name|Path
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" as parameter not found"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Error creating "
operator|+
name|clazzName
operator|+
literal|" instance"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Loads a specific Directory implementation     * @param clazzName The name of the Directory class to load    * @return The Directory class loaded    * @throws ClassNotFoundException If the specified class cannot be found.    */
DECL|method|loadDirectoryClass
specifier|public
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|Directory
argument_list|>
name|loadDirectoryClass
parameter_list|(
name|String
name|clazzName
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
return|return
name|Class
operator|.
name|forName
argument_list|(
name|adjustDirectoryClassName
argument_list|(
name|clazzName
argument_list|)
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|Directory
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**    * Loads a specific FSDirectory implementation    * @param clazzName The name of the FSDirectory class to load    * @return The FSDirectory class loaded    * @throws ClassNotFoundException If the specified class cannot be found.    */
DECL|method|loadFSDirectoryClass
specifier|public
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|FSDirectory
argument_list|>
name|loadFSDirectoryClass
parameter_list|(
name|String
name|clazzName
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
return|return
name|Class
operator|.
name|forName
argument_list|(
name|adjustDirectoryClassName
argument_list|(
name|clazzName
argument_list|)
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|FSDirectory
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|adjustDirectoryClassName
specifier|private
specifier|static
name|String
name|adjustDirectoryClassName
parameter_list|(
name|String
name|clazzName
parameter_list|)
block|{
if|if
condition|(
name|clazzName
operator|==
literal|null
operator|||
name|clazzName
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The "
operator|+
name|FSDirectory
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" implementation cannot be null or empty"
argument_list|)
throw|;
block|}
if|if
condition|(
name|clazzName
operator|.
name|indexOf
argument_list|(
literal|"."
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
comment|// if not fully qualified, assume .store
name|clazzName
operator|=
name|Directory
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|clazzName
expr_stmt|;
block|}
return|return
name|clazzName
return|;
block|}
comment|/**    * Creates a new specific FSDirectory instance    * @param clazz The class of the object to be created    * @param path The file to be used as parameter constructor    * @return The new FSDirectory instance    * @throws NoSuchMethodException If the Directory does not have a constructor that takes<code>Path</code>.    * @throws InstantiationException If the class is abstract or an interface.    * @throws IllegalAccessException If the constructor does not have public visibility.    * @throws InvocationTargetException If the constructor throws an exception    */
DECL|method|newFSDirectory
specifier|public
specifier|static
name|FSDirectory
name|newFSDirectory
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|FSDirectory
argument_list|>
name|clazz
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|NoSuchMethodException
throws|,
name|InstantiationException
throws|,
name|IllegalAccessException
throws|,
name|InvocationTargetException
block|{
comment|// Assuming every FSDirectory has a ctor(Path):
name|Constructor
argument_list|<
name|?
extends|extends
name|FSDirectory
argument_list|>
name|ctor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|Path
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|ctor
operator|.
name|newInstance
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
end_class

end_unit

