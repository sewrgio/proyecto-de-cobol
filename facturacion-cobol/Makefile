COBC = cobc
FLAGS = -I copybooks/ -free
BUILD_DIR = build
TARGET = tests/run_test

SRCS = $(wildcard src/core/*.cbl) $(wildcard src/db/*.cbl) $(wildcard src/api/*.cbl)
OBJS = $(patsubst src/%.cbl, $(BUILD_DIR)/%.o, $(SRCS))

all: $(TARGET)

$(BUILD_DIR):
	mkdir -p $(BUILD_DIR)/core $(BUILD_DIR)/db $(BUILD_DIR)/api

$(BUILD_DIR)/%.o: src/%.cbl | $(BUILD_DIR)
	$(COBC) -c $(FLAGS) $< -o $@

$(TARGET): $(OBJS)
	$(COBC) -x $(FLAGS) tests/test_factura.cbl $(BUILD_DIR)/*/*.o -o $(TARGET)

run: $(TARGET)
	./$(TARGET)

clean:
	rm -rf $(BUILD_DIR) $(TARGET)

.PHONY: all run clean
