begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Array
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
name|Field
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
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormatSymbols
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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

begin_comment
comment|/**  * Estimates the size (memory representation) of Java objects.  *<p>  * This class uses assumptions that were discovered for the Hotspot  * virtual machine. If you use a non-OpenJDK/Oracle-based JVM,  * the measurements may be slightly wrong.  *   * @see #shallowSizeOf(Object)  * @see #shallowSizeOfInstance(Class)  *   * @lucene.internal  */
end_comment

begin_class
DECL|class|RamUsageEstimator
specifier|public
specifier|final
class|class
name|RamUsageEstimator
block|{
comment|/** One kilobyte bytes. */
DECL|field|ONE_KB
specifier|public
specifier|static
specifier|final
name|long
name|ONE_KB
init|=
literal|1024
decl_stmt|;
comment|/** One megabyte bytes. */
DECL|field|ONE_MB
specifier|public
specifier|static
specifier|final
name|long
name|ONE_MB
init|=
name|ONE_KB
operator|*
name|ONE_KB
decl_stmt|;
comment|/** One gigabyte bytes.*/
DECL|field|ONE_GB
specifier|public
specifier|static
specifier|final
name|long
name|ONE_GB
init|=
name|ONE_KB
operator|*
name|ONE_MB
decl_stmt|;
comment|/** No instantiation. */
DECL|method|RamUsageEstimator
specifier|private
name|RamUsageEstimator
parameter_list|()
block|{}
comment|/**     * Number of bytes used to represent a {@code boolean} in binary form    * @deprecated use {@code 1} instead.    */
annotation|@
name|Deprecated
DECL|field|NUM_BYTES_BOOLEAN
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_BOOLEAN
init|=
literal|1
decl_stmt|;
comment|/**     * Number of bytes used to represent a {@code byte} in binary form    * @deprecated use {@code 1} instead.    */
annotation|@
name|Deprecated
DECL|field|NUM_BYTES_BYTE
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_BYTE
init|=
literal|1
decl_stmt|;
comment|/**     * Number of bytes used to represent a {@code char} in binary form    * @deprecated use {@link Character#BYTES} instead.    */
annotation|@
name|Deprecated
DECL|field|NUM_BYTES_CHAR
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_CHAR
init|=
name|Character
operator|.
name|BYTES
decl_stmt|;
comment|/**     * Number of bytes used to represent a {@code short} in binary form    * @deprecated use {@link Short#BYTES} instead.    */
annotation|@
name|Deprecated
DECL|field|NUM_BYTES_SHORT
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_SHORT
init|=
name|Short
operator|.
name|BYTES
decl_stmt|;
comment|/**     * Number of bytes used to represent an {@code int} in binary form    * @deprecated use {@link Integer#BYTES} instead.    */
annotation|@
name|Deprecated
DECL|field|NUM_BYTES_INT
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_INT
init|=
name|Integer
operator|.
name|BYTES
decl_stmt|;
comment|/**     * Number of bytes used to represent a {@code float} in binary form    * @deprecated use {@link Float#BYTES} instead.    */
annotation|@
name|Deprecated
DECL|field|NUM_BYTES_FLOAT
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_FLOAT
init|=
name|Float
operator|.
name|BYTES
decl_stmt|;
comment|/**     * Number of bytes used to represent a {@code long} in binary form    * @deprecated use {@link Long#BYTES} instead.    */
annotation|@
name|Deprecated
DECL|field|NUM_BYTES_LONG
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_LONG
init|=
name|Long
operator|.
name|BYTES
decl_stmt|;
comment|/**     * Number of bytes used to represent a {@code double} in binary form    * @deprecated use {@link Double#BYTES} instead.    */
annotation|@
name|Deprecated
DECL|field|NUM_BYTES_DOUBLE
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_DOUBLE
init|=
name|Double
operator|.
name|BYTES
decl_stmt|;
comment|/**     * True, iff compressed references (oops) are enabled by this JVM     */
DECL|field|COMPRESSED_REFS_ENABLED
specifier|public
specifier|final
specifier|static
name|boolean
name|COMPRESSED_REFS_ENABLED
decl_stmt|;
comment|/**     * Number of bytes this JVM uses to represent an object reference.     */
DECL|field|NUM_BYTES_OBJECT_REF
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_OBJECT_REF
decl_stmt|;
comment|/**    * Number of bytes to represent an object header (no fields, no alignments).    */
DECL|field|NUM_BYTES_OBJECT_HEADER
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_OBJECT_HEADER
decl_stmt|;
comment|/**    * Number of bytes to represent an array header (no content, but with alignments).    */
DECL|field|NUM_BYTES_ARRAY_HEADER
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_ARRAY_HEADER
decl_stmt|;
comment|/**    * A constant specifying the object alignment boundary inside the JVM. Objects will    * always take a full multiple of this constant, possibly wasting some space.     */
DECL|field|NUM_BYTES_OBJECT_ALIGNMENT
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_OBJECT_ALIGNMENT
decl_stmt|;
comment|/**    * Sizes of primitive classes.    */
DECL|field|primitiveSizes
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Integer
argument_list|>
name|primitiveSizes
init|=
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|primitiveSizes
operator|.
name|put
argument_list|(
name|boolean
operator|.
name|class
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|primitiveSizes
operator|.
name|put
argument_list|(
name|byte
operator|.
name|class
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|primitiveSizes
operator|.
name|put
argument_list|(
name|char
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|Character
operator|.
name|BYTES
argument_list|)
argument_list|)
expr_stmt|;
name|primitiveSizes
operator|.
name|put
argument_list|(
name|short
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|Short
operator|.
name|BYTES
argument_list|)
argument_list|)
expr_stmt|;
name|primitiveSizes
operator|.
name|put
argument_list|(
name|int
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|)
argument_list|)
expr_stmt|;
name|primitiveSizes
operator|.
name|put
argument_list|(
name|float
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|Float
operator|.
name|BYTES
argument_list|)
argument_list|)
expr_stmt|;
name|primitiveSizes
operator|.
name|put
argument_list|(
name|double
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|Double
operator|.
name|BYTES
argument_list|)
argument_list|)
expr_stmt|;
name|primitiveSizes
operator|.
name|put
argument_list|(
name|long
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|Long
operator|.
name|BYTES
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * JVMs typically cache small longs. This tries to find out what the range is.    */
DECL|field|LONG_CACHE_MIN_VALUE
DECL|field|LONG_CACHE_MAX_VALUE
specifier|static
specifier|final
name|long
name|LONG_CACHE_MIN_VALUE
decl_stmt|,
name|LONG_CACHE_MAX_VALUE
decl_stmt|;
DECL|field|LONG_SIZE
specifier|static
specifier|final
name|int
name|LONG_SIZE
decl_stmt|;
comment|/** For testing only */
DECL|field|JVM_IS_HOTSPOT_64BIT
specifier|static
specifier|final
name|boolean
name|JVM_IS_HOTSPOT_64BIT
decl_stmt|;
DECL|field|MANAGEMENT_FACTORY_CLASS
specifier|static
specifier|final
name|String
name|MANAGEMENT_FACTORY_CLASS
init|=
literal|"java.lang.management.ManagementFactory"
decl_stmt|;
DECL|field|HOTSPOT_BEAN_CLASS
specifier|static
specifier|final
name|String
name|HOTSPOT_BEAN_CLASS
init|=
literal|"com.sun.management.HotSpotDiagnosticMXBean"
decl_stmt|;
comment|/**    * Initialize constants and try to collect information about the JVM internals.     */
static|static
block|{
if|if
condition|(
name|Constants
operator|.
name|JRE_IS_64BIT
condition|)
block|{
comment|// Try to get compressed oops and object alignment (the default seems to be 8 on Hotspot);
comment|// (this only works on 64 bit, on 32 bits the alignment and reference size is fixed):
name|boolean
name|compressedOops
init|=
literal|false
decl_stmt|;
name|int
name|objectAlignment
init|=
literal|8
decl_stmt|;
name|boolean
name|isHotspot
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|beanClazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|HOTSPOT_BEAN_CLASS
argument_list|)
decl_stmt|;
comment|// we use reflection for this, because the management factory is not part
comment|// of Java 8's compact profile:
specifier|final
name|Object
name|hotSpotBean
init|=
name|Class
operator|.
name|forName
argument_list|(
name|MANAGEMENT_FACTORY_CLASS
argument_list|)
operator|.
name|getMethod
argument_list|(
literal|"getPlatformMXBean"
argument_list|,
name|Class
operator|.
name|class
argument_list|)
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
name|beanClazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|hotSpotBean
operator|!=
literal|null
condition|)
block|{
name|isHotspot
operator|=
literal|true
expr_stmt|;
specifier|final
name|Method
name|getVMOptionMethod
init|=
name|beanClazz
operator|.
name|getMethod
argument_list|(
literal|"getVMOption"
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Object
name|vmOption
init|=
name|getVMOptionMethod
operator|.
name|invoke
argument_list|(
name|hotSpotBean
argument_list|,
literal|"UseCompressedOops"
argument_list|)
decl_stmt|;
name|compressedOops
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|vmOption
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"getValue"
argument_list|)
operator|.
name|invoke
argument_list|(
name|vmOption
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
decl||
name|RuntimeException
name|e
parameter_list|)
block|{
name|isHotspot
operator|=
literal|false
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|Object
name|vmOption
init|=
name|getVMOptionMethod
operator|.
name|invoke
argument_list|(
name|hotSpotBean
argument_list|,
literal|"ObjectAlignmentInBytes"
argument_list|)
decl_stmt|;
name|objectAlignment
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|vmOption
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"getValue"
argument_list|)
operator|.
name|invoke
argument_list|(
name|vmOption
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
decl||
name|RuntimeException
name|e
parameter_list|)
block|{
name|isHotspot
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
decl||
name|RuntimeException
name|e
parameter_list|)
block|{
name|isHotspot
operator|=
literal|false
expr_stmt|;
block|}
name|JVM_IS_HOTSPOT_64BIT
operator|=
name|isHotspot
expr_stmt|;
name|COMPRESSED_REFS_ENABLED
operator|=
name|compressedOops
expr_stmt|;
name|NUM_BYTES_OBJECT_ALIGNMENT
operator|=
name|objectAlignment
expr_stmt|;
comment|// reference size is 4, if we have compressed oops:
name|NUM_BYTES_OBJECT_REF
operator|=
name|COMPRESSED_REFS_ENABLED
condition|?
literal|4
else|:
literal|8
expr_stmt|;
comment|// "best guess" based on reference size:
name|NUM_BYTES_OBJECT_HEADER
operator|=
literal|8
operator|+
name|NUM_BYTES_OBJECT_REF
expr_stmt|;
comment|// array header is NUM_BYTES_OBJECT_HEADER + NUM_BYTES_INT, but aligned (object alignment):
name|NUM_BYTES_ARRAY_HEADER
operator|=
operator|(
name|int
operator|)
name|alignObjectSize
argument_list|(
name|NUM_BYTES_OBJECT_HEADER
operator|+
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|JVM_IS_HOTSPOT_64BIT
operator|=
literal|false
expr_stmt|;
name|COMPRESSED_REFS_ENABLED
operator|=
literal|false
expr_stmt|;
name|NUM_BYTES_OBJECT_ALIGNMENT
operator|=
literal|8
expr_stmt|;
name|NUM_BYTES_OBJECT_REF
operator|=
literal|4
expr_stmt|;
name|NUM_BYTES_OBJECT_HEADER
operator|=
literal|8
expr_stmt|;
comment|// For 32 bit JVMs, no extra alignment of array header:
name|NUM_BYTES_ARRAY_HEADER
operator|=
name|NUM_BYTES_OBJECT_HEADER
operator|+
name|Integer
operator|.
name|BYTES
expr_stmt|;
block|}
comment|// get min/max value of cached Long class instances:
name|long
name|longCacheMinValue
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|longCacheMinValue
operator|>
name|Long
operator|.
name|MIN_VALUE
operator|&&
name|Long
operator|.
name|valueOf
argument_list|(
name|longCacheMinValue
operator|-
literal|1
argument_list|)
operator|==
name|Long
operator|.
name|valueOf
argument_list|(
name|longCacheMinValue
operator|-
literal|1
argument_list|)
condition|)
block|{
name|longCacheMinValue
operator|-=
literal|1
expr_stmt|;
block|}
name|long
name|longCacheMaxValue
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|longCacheMaxValue
operator|<
name|Long
operator|.
name|MAX_VALUE
operator|&&
name|Long
operator|.
name|valueOf
argument_list|(
name|longCacheMaxValue
operator|+
literal|1
argument_list|)
operator|==
name|Long
operator|.
name|valueOf
argument_list|(
name|longCacheMaxValue
operator|+
literal|1
argument_list|)
condition|)
block|{
name|longCacheMaxValue
operator|+=
literal|1
expr_stmt|;
block|}
name|LONG_CACHE_MIN_VALUE
operator|=
name|longCacheMinValue
expr_stmt|;
name|LONG_CACHE_MAX_VALUE
operator|=
name|longCacheMaxValue
expr_stmt|;
name|LONG_SIZE
operator|=
operator|(
name|int
operator|)
name|shallowSizeOfInstance
argument_list|(
name|Long
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**     * Aligns an object size to be the next multiple of {@link #NUM_BYTES_OBJECT_ALIGNMENT}.     */
DECL|method|alignObjectSize
specifier|public
specifier|static
name|long
name|alignObjectSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|size
operator|+=
operator|(
name|long
operator|)
name|NUM_BYTES_OBJECT_ALIGNMENT
operator|-
literal|1L
expr_stmt|;
return|return
name|size
operator|-
operator|(
name|size
operator|%
name|NUM_BYTES_OBJECT_ALIGNMENT
operator|)
return|;
block|}
comment|/**    * Return the size of the provided {@link Long} object, returning 0 if it is    * cached by the JVM and its shallow size otherwise.    */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|Long
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|>=
name|LONG_CACHE_MIN_VALUE
operator|&&
name|value
operator|<=
name|LONG_CACHE_MAX_VALUE
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|LONG_SIZE
return|;
block|}
comment|/** Returns the size in bytes of the byte[] object. */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|byte
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns the size in bytes of the boolean[] object. */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|boolean
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns the size in bytes of the char[] object. */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|char
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|long
operator|)
name|Character
operator|.
name|BYTES
operator|*
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns the size in bytes of the short[] object. */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|short
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|long
operator|)
name|Short
operator|.
name|BYTES
operator|*
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns the size in bytes of the int[] object. */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|int
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|long
operator|)
name|Integer
operator|.
name|BYTES
operator|*
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns the size in bytes of the float[] object. */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|float
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|long
operator|)
name|Float
operator|.
name|BYTES
operator|*
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns the size in bytes of the long[] object. */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|long
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|long
operator|)
name|Long
operator|.
name|BYTES
operator|*
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns the size in bytes of the double[] object. */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|double
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|long
operator|)
name|Double
operator|.
name|BYTES
operator|*
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns the shallow size in bytes of the Object[] object. */
comment|// Use this method instead of #shallowSizeOf(Object) to avoid costly reflection
DECL|method|shallowSizeOf
specifier|public
specifier|static
name|long
name|shallowSizeOf
parameter_list|(
name|Object
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|long
operator|)
name|NUM_BYTES_OBJECT_REF
operator|*
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**     * Estimates a "shallow" memory usage of the given object. For arrays, this will be the    * memory taken by array storage (no subreferences will be followed). For objects, this    * will be the memory taken by the fields.    *     * JVM object alignments are also applied.    */
DECL|method|shallowSizeOf
specifier|public
specifier|static
name|long
name|shallowSizeOf
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|0
return|;
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clz
init|=
name|obj
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|clz
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
name|shallowSizeOfArray
argument_list|(
name|obj
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|shallowSizeOfInstance
argument_list|(
name|clz
argument_list|)
return|;
block|}
block|}
comment|/**    * Returns the shallow instance size in bytes an instance of the given class would occupy.    * This works with all conventional classes and primitive types, but not with arrays    * (the size then depends on the number of elements and varies from object to object).    *     * @see #shallowSizeOf(Object)    * @throws IllegalArgumentException if {@code clazz} is an array class.     */
DECL|method|shallowSizeOfInstance
specifier|public
specifier|static
name|long
name|shallowSizeOfInstance
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|.
name|isArray
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"This method does not work with array classes."
argument_list|)
throw|;
if|if
condition|(
name|clazz
operator|.
name|isPrimitive
argument_list|()
condition|)
return|return
name|primitiveSizes
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
return|;
name|long
name|size
init|=
name|NUM_BYTES_OBJECT_HEADER
decl_stmt|;
comment|// Walk type hierarchy
for|for
control|(
init|;
name|clazz
operator|!=
literal|null
condition|;
name|clazz
operator|=
name|clazz
operator|.
name|getSuperclass
argument_list|()
control|)
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|target
init|=
name|clazz
decl_stmt|;
specifier|final
name|Field
index|[]
name|fields
init|=
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|Field
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Field
index|[]
name|run
parameter_list|()
block|{
return|return
name|target
operator|.
name|getDeclaredFields
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|Field
name|f
range|:
name|fields
control|)
block|{
if|if
condition|(
operator|!
name|Modifier
operator|.
name|isStatic
argument_list|(
name|f
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
name|size
operator|=
name|adjustForField
argument_list|(
name|size
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|alignObjectSize
argument_list|(
name|size
argument_list|)
return|;
block|}
comment|/**    * Return shallow size of any<code>array</code>.    */
DECL|method|shallowSizeOfArray
specifier|private
specifier|static
name|long
name|shallowSizeOfArray
parameter_list|(
name|Object
name|array
parameter_list|)
block|{
name|long
name|size
init|=
name|NUM_BYTES_ARRAY_HEADER
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|Array
operator|.
name|getLength
argument_list|(
name|array
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|arrayElementClazz
init|=
name|array
operator|.
name|getClass
argument_list|()
operator|.
name|getComponentType
argument_list|()
decl_stmt|;
if|if
condition|(
name|arrayElementClazz
operator|.
name|isPrimitive
argument_list|()
condition|)
block|{
name|size
operator|+=
operator|(
name|long
operator|)
name|len
operator|*
name|primitiveSizes
operator|.
name|get
argument_list|(
name|arrayElementClazz
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|size
operator|+=
operator|(
name|long
operator|)
name|NUM_BYTES_OBJECT_REF
operator|*
name|len
expr_stmt|;
block|}
block|}
return|return
name|alignObjectSize
argument_list|(
name|size
argument_list|)
return|;
block|}
comment|/**    * This method returns the maximum representation size of an object.<code>sizeSoFar</code>    * is the object's size measured so far.<code>f</code> is the field being probed.    *     *<p>The returned offset will be the maximum of whatever was measured so far and     *<code>f</code> field's offset and representation size (unaligned).    */
DECL|method|adjustForField
specifier|static
name|long
name|adjustForField
parameter_list|(
name|long
name|sizeSoFar
parameter_list|,
specifier|final
name|Field
name|f
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|type
init|=
name|f
operator|.
name|getType
argument_list|()
decl_stmt|;
specifier|final
name|int
name|fsize
init|=
name|type
operator|.
name|isPrimitive
argument_list|()
condition|?
name|primitiveSizes
operator|.
name|get
argument_list|(
name|type
argument_list|)
else|:
name|NUM_BYTES_OBJECT_REF
decl_stmt|;
comment|// TODO: No alignments based on field type/ subclass fields alignments?
return|return
name|sizeSoFar
operator|+
name|fsize
return|;
block|}
comment|/**    * Returns<code>size</code> in human-readable units (GB, MB, KB or bytes).    */
DECL|method|humanReadableUnits
specifier|public
specifier|static
name|String
name|humanReadableUnits
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
return|return
name|humanReadableUnits
argument_list|(
name|bytes
argument_list|,
operator|new
name|DecimalFormat
argument_list|(
literal|"0.#"
argument_list|,
name|DecimalFormatSymbols
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns<code>size</code> in human-readable units (GB, MB, KB or bytes).     */
DECL|method|humanReadableUnits
specifier|public
specifier|static
name|String
name|humanReadableUnits
parameter_list|(
name|long
name|bytes
parameter_list|,
name|DecimalFormat
name|df
parameter_list|)
block|{
if|if
condition|(
name|bytes
operator|/
name|ONE_GB
operator|>
literal|0
condition|)
block|{
return|return
name|df
operator|.
name|format
argument_list|(
operator|(
name|float
operator|)
name|bytes
operator|/
name|ONE_GB
argument_list|)
operator|+
literal|" GB"
return|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|/
name|ONE_MB
operator|>
literal|0
condition|)
block|{
return|return
name|df
operator|.
name|format
argument_list|(
operator|(
name|float
operator|)
name|bytes
operator|/
name|ONE_MB
argument_list|)
operator|+
literal|" MB"
return|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|/
name|ONE_KB
operator|>
literal|0
condition|)
block|{
return|return
name|df
operator|.
name|format
argument_list|(
operator|(
name|float
operator|)
name|bytes
operator|/
name|ONE_KB
argument_list|)
operator|+
literal|" KB"
return|;
block|}
else|else
block|{
return|return
name|bytes
operator|+
literal|" bytes"
return|;
block|}
block|}
comment|/**    * Return the size of the provided array of {@link Accountable}s by summing    * up the shallow size of the array and the    * {@link Accountable#ramBytesUsed() memory usage} reported by each    * {@link Accountable}.    */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|Accountable
index|[]
name|accountables
parameter_list|)
block|{
name|long
name|size
init|=
name|shallowSizeOf
argument_list|(
name|accountables
argument_list|)
decl_stmt|;
for|for
control|(
name|Accountable
name|accountable
range|:
name|accountables
control|)
block|{
if|if
condition|(
name|accountable
operator|!=
literal|null
condition|)
block|{
name|size
operator|+=
name|accountable
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|size
return|;
block|}
block|}
end_class

end_unit

