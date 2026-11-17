import React from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';

export interface RadioOption<T extends string> {
  label: string;
  value: T;
}

interface RadioGroupProps<T extends string> {
  options: RadioOption<T>[];
  selectedValue: T;
  onSelect: (value: T) => void;
}

export function RadioGroup<T extends string>({
  options,
  selectedValue,
  onSelect,
}: RadioGroupProps<T>) {
  return (
    <View style={styles.container} accessibilityRole="radiogroup">
      {options.map((option) => {
        const isSelected = option.value === selectedValue;

        return (
          <Pressable
            key={option.value}
            style={styles.optionRow}
            onPress={() => onSelect(option.value)}
            accessibilityRole="radio"
            accessibilityState={{ checked: isSelected }}
          >
            <View style={[styles.outerCircle, isSelected && styles.outerSelected]}>
              {isSelected && <View style={styles.innerCircle} />}
            </View>
            <Text style={styles.label}>{option.label}</Text>
          </Pressable>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    gap: 12,
  },
  optionRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
  },
  outerCircle: {
    height: 20,
    width: 20,
    borderRadius: 10,
    borderWidth: 2,
    borderColor: '#666',
    alignItems: 'center',
    justifyContent: 'center',
  },
  outerSelected: {
    borderColor: '#007AFF',
  },
  innerCircle: {
    height: 10,
    width: 10,
    borderRadius: 5,
    backgroundColor: '#007AFF',
  },
  label: {
    fontSize: 16,
  },
});
