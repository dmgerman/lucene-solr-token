begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|utils
package|;
end_package

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

begin_comment
comment|/**  * A collection of static helper methods solve common reflection problems  *   * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|ReflectionUtils
specifier|public
class|class
name|ReflectionUtils
block|{
comment|/**      * Check if the given type implements a given super type      * @param typeToCheck - type supposed to implement an interface      * @param superType - the interface to be implemented by the type to check      * @return<code>true</code> if and only if the super type is above in the type hierarchy of the given type, otherwise<code>false</code>      */
DECL|method|implementsType
specifier|public
specifier|static
name|boolean
name|implementsType
parameter_list|(
name|Class
name|typeToCheck
parameter_list|,
name|Class
name|superType
parameter_list|)
block|{
if|if
condition|(
name|superType
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|superType
operator|.
name|isInterface
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|typeToCheck
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|typeToCheck
operator|.
name|equals
argument_list|(
name|Object
operator|.
name|class
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|typeToCheck
operator|.
name|equals
argument_list|(
name|superType
argument_list|)
condition|)
return|return
literal|true
return|;
name|Class
index|[]
name|interfaces
init|=
name|typeToCheck
operator|.
name|getInterfaces
argument_list|()
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
name|interfaces
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|implementsType
argument_list|(
name|interfaces
index|[
name|i
index|]
argument_list|,
name|superType
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
name|implementsType
argument_list|(
name|typeToCheck
operator|.
name|getSuperclass
argument_list|()
argument_list|,
name|superType
argument_list|)
return|;
block|}
comment|/**      * Check if the given type extends a given super type      * @param typeToCheck - type supposed to extend an specific type      * @param superType - the type to be extended by the type to check      * @return<code>true</code> if and only if the super type is above in the type hierarchy of the given type, otherwise<code>false</code>      */
DECL|method|extendsType
specifier|public
specifier|static
name|boolean
name|extendsType
parameter_list|(
name|Class
name|typeToCheck
parameter_list|,
name|Class
name|superType
parameter_list|)
block|{
if|if
condition|(
name|typeToCheck
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|typeToCheck
operator|.
name|equals
argument_list|(
name|Object
operator|.
name|class
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|typeToCheck
operator|.
name|equals
argument_list|(
name|superType
argument_list|)
condition|)
return|return
literal|true
return|;
return|return
name|extendsType
argument_list|(
name|typeToCheck
operator|.
name|getSuperclass
argument_list|()
argument_list|,
name|superType
argument_list|)
return|;
block|}
comment|/**      * This method combines the extendsType and implementsType and checks interfaces and classes      * @param typeToCheck - type supposed to extend / implement an specific type      * @param superType - the type to be extended / implemented by the type to check      * @return<code>true</code> if and only if the super type is above in the type hierarchy of the given type, otherwise<code>false</code>      */
DECL|method|isTypeOf
specifier|public
specifier|static
name|boolean
name|isTypeOf
parameter_list|(
name|Class
name|typeToCheck
parameter_list|,
name|Class
name|superType
parameter_list|)
block|{
return|return
name|extendsType
argument_list|(
name|typeToCheck
argument_list|,
name|superType
argument_list|)
operator|||
name|implementsType
argument_list|(
name|typeToCheck
argument_list|,
name|superType
argument_list|)
return|;
block|}
comment|/**      * @param type - the type to check      * @param parameter - the constructor parameter      * @return<code>true</code> if and only if the type has a visible constructor with the desired parameters      */
DECL|method|hasDesiredConstructor
specifier|public
specifier|static
name|boolean
name|hasDesiredConstructor
parameter_list|(
name|Class
name|type
parameter_list|,
name|Class
index|[]
name|parameter
parameter_list|)
block|{
try|try
block|{
return|return
name|type
operator|.
name|getConstructor
argument_list|(
name|parameter
argument_list|)
operator|!=
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**  * @param<T> the type of the class to instantiate   * @param clazz - class object of the type  * @return a new instance of T   */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getDefaultInstance
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|getDefaultInstance
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ReflectionException
argument_list|(
literal|"class must not be null"
argument_list|)
throw|;
try|try
block|{
name|Constructor
name|constructor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
operator|new
name|Class
index|[]
block|{}
argument_list|)
decl_stmt|;
return|return
operator|(
name|T
operator|)
name|constructor
operator|.
name|newInstance
argument_list|(
operator|new
name|Object
index|[]
block|{}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ReflectionException
argument_list|(
literal|"can not instantiate type of class "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**  * This method calls {@link Class#newInstance()} to get a new instance. Use with care!  * @param clazz - the class to instantiate  * @return<code>true</code> if an instance could be created, otherwise false;  */
DECL|method|canCreateInstance
specifier|public
specifier|static
name|boolean
name|canCreateInstance
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|clazz
operator|.
name|isPrimitive
argument_list|()
condition|)
name|clazz
operator|=
name|getPrimitiveWrapper
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
try|try
block|{
name|Object
name|o
init|=
name|clazz
operator|.
name|newInstance
argument_list|()
decl_stmt|;
return|return
name|o
operator|!=
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**  * Returns the wrapper type for the given primitive type. Wrappers can be  * easily instantiated via reflection and will be boxed by the VM  * @param primitive - the primitive type   * @return - the corresponding wrapper type  */
DECL|method|getPrimitiveWrapper
specifier|public
specifier|static
specifier|final
name|Class
name|getPrimitiveWrapper
parameter_list|(
name|Class
name|primitive
parameter_list|)
block|{
if|if
condition|(
name|primitive
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ReflectionException
argument_list|(
literal|"primitive must not be null"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|primitive
operator|.
name|isPrimitive
argument_list|()
condition|)
throw|throw
operator|new
name|ReflectionException
argument_list|(
literal|"given class is not a primitive"
argument_list|)
throw|;
if|if
condition|(
name|primitive
operator|==
name|Integer
operator|.
name|TYPE
condition|)
return|return
name|Integer
operator|.
name|class
return|;
if|if
condition|(
name|primitive
operator|==
name|Float
operator|.
name|TYPE
condition|)
return|return
name|Float
operator|.
name|class
return|;
if|if
condition|(
name|primitive
operator|==
name|Long
operator|.
name|TYPE
condition|)
return|return
name|Long
operator|.
name|class
return|;
if|if
condition|(
name|primitive
operator|==
name|Short
operator|.
name|TYPE
condition|)
return|return
name|Short
operator|.
name|class
return|;
if|if
condition|(
name|primitive
operator|==
name|Byte
operator|.
name|TYPE
condition|)
return|return
name|Byte
operator|.
name|class
return|;
if|if
condition|(
name|primitive
operator|==
name|Double
operator|.
name|TYPE
condition|)
return|return
name|Double
operator|.
name|class
return|;
if|if
condition|(
name|primitive
operator|==
name|Boolean
operator|.
name|TYPE
condition|)
return|return
name|Boolean
operator|.
name|class
return|;
return|return
name|primitive
return|;
block|}
comment|/**  * Exception wrapper for all thrown exception in the ReflectionUtils methods  * @author Simon Willnauer  *  */
DECL|class|ReflectionException
specifier|public
specifier|static
class|class
name|ReflectionException
extends|extends
name|RuntimeException
block|{
comment|/**      *       */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|4855060602565614280L
decl_stmt|;
comment|/**      * @param message -  the exception message      * @param cause - the exception root cause      */
DECL|method|ReflectionException
specifier|public
name|ReflectionException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
comment|/**      * @param message - the exception message      */
DECL|method|ReflectionException
specifier|public
name|ReflectionException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
block|}
block|}
end_class

end_unit

