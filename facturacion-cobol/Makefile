COBC = cobc
FLAGS = -I copybooks/ -free
BUILD_DIR = build
TARGET = tests/run_test
JSON_BIN = bin/proc-factura-json

SRCS = $(wildcard src/core/*.cbl) $(wildcard src/db/*.cbl) $(wildcard src/api/PROC-FACTURA.cbl)
OBJS = $(patsubst src/%.cbl, $(BUILD_DIR)/%.o, $(SRCS))

all: $(TARGET) $(JSON_BIN)

$(BUILD_DIR):
	mkdir -p $(BUILD_DIR)/core $(BUILD_DIR)/db $(BUILD_DIR)/api

$(BUILD_DIR)/%.o: src/%.cbl | $(BUILD_DIR)
	$(COBC) -c $(FLAGS) $< -o $@

$(TARGET): $(OBJS)
	$(COBC) -x $(FLAGS) tests/test_factura.cbl $(BUILD_DIR)/*/*.o -o $(TARGET)

$(JSON_BIN): $(OBJS)
	mkdir -p bin
	$(COBC) -x $(FLAGS) src/api/PROC-FACTURA-JSON.cbl $(BUILD_DIR)/*/*.o -o $(JSON_BIN)

run: $(TARGET)
	./$(TARGET)

run-json: $(JSON_BIN)
	./$(JSON_BIN)

clean:
	rm -rf $(BUILD_DIR) $(TARGET) bin

.PHONY: all run run-json clean
