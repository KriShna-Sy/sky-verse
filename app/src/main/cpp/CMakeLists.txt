cmake_minimum_required(VERSION 3.22.1)
project("skyverse_jni" C CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)  # static llama libs linked into a .so

# ---------------------------------------------------------------------------
# llama.cpp source (pinned). Fetch once with:
#   git clone --depth 1 --branch b12212 https://github.com/ggml-org/llama.cpp app/src/main/cpp/llama.cpp
# ---------------------------------------------------------------------------
set(LLAMA_DIR ${CMAKE_CURRENT_LIST_DIR}/llama.cpp)
if(NOT EXISTS ${LLAMA_DIR}/CMakeLists.txt)
    message(FATAL_ERROR
        "llama.cpp not found at ${LLAMA_DIR}\n"
        "Run from the project root:\n"
        "  git clone --depth 1 --branch b12212 https://github.com/ggml-org/llama.cpp app/src/main/cpp/llama.cpp")
endif()

# Lean, static, CPU-only build
set(BUILD_SHARED_LIBS     OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_COMMON    OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_TESTS     OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_TOOLS     OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_EXAMPLES  OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_SERVER    OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_APP       OFF CACHE BOOL "" FORCE)
set(LLAMA_OPENSSL         OFF CACHE BOOL "" FORCE)
set(GGML_NATIVE           OFF CACHE BOOL "" FORCE)
set(GGML_OPENMP           OFF CACHE BOOL "" FORCE)
set(GGML_BACKEND_DL       OFF CACHE BOOL "" FORCE)
if(ANDROID_ABI STREQUAL "arm64-v8a")
    # dotprod + fp16: every Snapdragon 845+/Dimensity/Exynos 9810+ class core
    set(GGML_CPU_ARM_ARCH "armv8.2-a+dotprod+fp16" CACHE STRING "" FORCE)
endif()

add_subdirectory(${LLAMA_DIR} build-llama)

add_library(skyverse_jni SHARED llama_jni.cpp)
target_link_libraries(skyverse_jni llama ggml android log)
