import { StyleSheet, Text, View } from 'react-native';

type CommentSectionProps = {
  name: string;
  comment: string;
};

export function CommentPage({ name, comment }: CommentSectionProps) {
  return (
    <View style={styles.card}>
      <Text style={styles.name}>{name}</Text>
      <Text>{comment}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    padding: 12,
    borderBottomWidth: StyleSheet.hairlineWidth,
  },
  name: {
    fontWeight: 'bold',
  },
});