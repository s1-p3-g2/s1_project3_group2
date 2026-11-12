import { FlatList, StyleSheet, Text, View } from 'react-native';

type Comment = {
  id: number;
  authorId: number;
  authorName: string;
  text: string;
  createdAt: string;
};

type CommentsSectionProps = {
  comments: Comment[];
};

export function CommentsSection({ comments }: CommentsSectionProps) {
  return (
    <View style={styles.section}>
      <Text style={styles.heading}>Comments</Text>

      <FlatList
        data={comments}
        keyExtractor={(item) => String(item.id)}
        renderItem={({ item }) => (
          <View style={styles.comment}>
            <Text style={styles.author}>{item.authorName}</Text>
            <Text>{item.text}</Text>
            <Text style={styles.date}>{item.createdAt}</Text>
          </View>
        )}
        ListEmptyComponent={<Text>No comments yet.</Text>}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  section: { gap: 12 },
  heading: { fontSize: 20, fontWeight: 'bold' },
  comment: { padding: 12, gap: 6, borderBottomWidth: StyleSheet.hairlineWidth },
  author: { fontWeight: 'bold' },
  date: { fontSize: 12, color: '#666' },
});