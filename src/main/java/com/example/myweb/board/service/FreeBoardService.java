package com.example.myweb.board.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.example.myweb.board.dto.FreeBoardDTO;
import com.example.myweb.board.entity.FreeBoardEntity;
import com.example.myweb.board.entity.FreeBoardFileEntity;
import com.example.myweb.board.entity.FreeBoardLikeEntity;
import com.example.myweb.board.repository.FreeBoardFileRepository;
import com.example.myweb.board.repository.FreeBoardLikeRepository;
import com.example.myweb.board.repository.FreeBoardRepository;
import com.example.myweb.user.entity.UserEntity;
import com.example.myweb.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

// DTO -> Entity
// Entity -> DTO

@Service
@RequiredArgsConstructor
public class FreeBoardService {
	private final FreeBoardRepository freeBoardRepository;
	private final FreeBoardFileRepository freeBoardFileRepository;
	private final FreeBoardLikeRepository freeBoardLikeRepository;
	private final UserRepository userRepository;

	public void save(FreeBoardDTO freeBoardDTO) throws IllegalStateException, IOException {
		// UserEntity를 UserRepository를 통해 조회합니다.
		UserEntity userEntity = userRepository
				.findByLoginidAndNickname(freeBoardDTO.getLoginid(), freeBoardDTO.getNickname()).get();

		// 파일 첨부 여부에 따라 로직 분리
		if (freeBoardDTO.getFreeboardFile().isEmpty()) {
			// 첨부 파일 없음
			FreeBoardEntity freeBoardEntity = FreeBoardEntity.toSaveEntity(freeBoardDTO, userEntity);
			freeBoardRepository.save(freeBoardEntity);
		} else {
			// 첨부 파일 있음
			FreeBoardEntity freeBoardEntity = FreeBoardEntity.toSaveFileEntity(freeBoardDTO, userEntity);
			Long savedSeq = freeBoardRepository.save(freeBoardEntity).getSeq(); // 게시글의 seq
			FreeBoardEntity freeBoard = freeBoardRepository.findById(savedSeq).get(); // 게시글의 정보를 가져옴
			for (MultipartFile freeBoardFile : freeBoardDTO.getFreeboardFile()) {
				String originalFilename = freeBoardFile.getOriginalFilename(); // 파일의 이름 가져옴
				String storedFileName = System.currentTimeMillis() + "_" + originalFilename; // 서버 저장용 이름 생성
				String savePath = new File("/src/main/resources/static/upload/").getAbsolutePath() + "/"
						+ storedFileName;

				File file = new File(savePath);
				file.getParentFile().mkdirs(); // 경로가 존재하지 않으면 생성
				freeBoardFile.transferTo(file); // 파일 저장

				FreeBoardFileEntity freeBoardFileEntity = FreeBoardFileEntity.toFreeBoardFileEntity(freeBoard,
						originalFilename, storedFileName);
				freeBoardFileRepository.save(freeBoardFileEntity); // 파일 정보 저장
			}
		}
	}
	



	@Transactional
	public List<FreeBoardDTO> findAll() {
		List<FreeBoardEntity> freeBoardEntityList = freeBoardRepository.findAll();
		List<FreeBoardDTO> freeBoardDTOList = new ArrayList<>();

		for (FreeBoardEntity freeBoardEntity : freeBoardEntityList) {
			freeBoardDTOList.add(FreeBoardDTO.toFreeBoardDTO(freeBoardEntity));
		}

		return freeBoardDTOList;
	}

	@Transactional
	public void incrementViews(Long seq) {
		freeBoardRepository.incrementViews(seq);
	}

	@Transactional
	public FreeBoardDTO findBySeq(Long seq) {
		Optional<FreeBoardEntity> optionalFreeBoardEntity = freeBoardRepository.findById(seq);
		if (optionalFreeBoardEntity.isPresent()) {
			FreeBoardEntity freeBoardEntity = optionalFreeBoardEntity.get();
			FreeBoardDTO freeBoardDTO = FreeBoardDTO.toFreeBoardDTO(freeBoardEntity);

			return freeBoardDTO;
		} else {
			return null;
		}
	}

	public void delete(Long seq) {
		freeBoardRepository.deleteById(seq);
	}
	
	@Transactional
	public FreeBoardDTO update(FreeBoardDTO freeBoardDTO) {
	    // 기존 게시글을 데이터베이스에서 조회
	    Optional<FreeBoardEntity> optionalBoard = freeBoardRepository.findById(freeBoardDTO.getSeq());
	    Optional<UserEntity> optionalUser = userRepository.findByLoginidAndNickname(freeBoardDTO.getLoginid(), freeBoardDTO.getNickname());

	    if (optionalBoard.isPresent() && optionalUser.isPresent()) {
	        FreeBoardEntity freeBoardEntity = optionalBoard.get();
	        UserEntity userEntity = optionalUser.get();

	        // 필요한 필드만 업데이트
	        freeBoardEntity.setTitle(freeBoardDTO.getTitle());
	        freeBoardEntity.setContents(freeBoardDTO.getContents());
	        freeBoardEntity.setTag(freeBoardDTO.getTag());

	        // 연관된 엔티티(댓글, 파일 등)는 건드리지 않음
	        // 추가적인 연관 엔티티 변경이 필요하면 적절히 처리 (add/remove)

	        // 업데이트된 엔티티 저장
	        freeBoardRepository.save(freeBoardEntity);

	        return FreeBoardDTO.toFreeBoardDTO(freeBoardEntity);
	    } else {
	        return null;
	    }
	}



	
	@Transactional
	public Page<FreeBoardDTO> paging(Pageable pageable, String tag, String search) {
	    int page = pageable.getPageNumber();
	    if (page < 0) {
	        page = 0;
	    } else {
	        page -= 1;
	    }
	    int pageLimit = 5; // 한 페이지에 보여줄 글 갯수

	    Page<FreeBoardEntity> freeBoardEntities;
	    
	    if (search != null && !search.isEmpty()) {
	        // 검색어가 있을 경우 제목으로 검색
	        freeBoardEntities = freeBoardRepository.findByTitleContaining(search, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "seq")));
	    } else if (tag != null && !tag.isEmpty()) {
	        // 태그가 있을 경우 해당 태그의 게시글을 가져옴
	        freeBoardEntities = freeBoardRepository.findByTag(tag, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "seq")));
	    } else {
	        // 태그와 검색어가 모두 없을 경우 전체 게시글을 가져옴
	        freeBoardEntities = freeBoardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "seq")));
	    }

	    Page<FreeBoardDTO> freeBoardDTOS = freeBoardEntities.map(freeBoard -> new FreeBoardDTO(
	        freeBoard.getSeq(),
	        freeBoard.getTag(),
	        freeBoard.getTitle(),
	        freeBoard.getCreatedTime(),
	        freeBoard.getViews(),
	        freeBoard.getLikeCount(),
	        freeBoard.getNickname()
	    ));

	    return freeBoardDTOS;
	}


	// 좋아요 기능
	@Transactional
	public boolean toggleLike(Long freeBoard_seq, String loginid) {
		// 게시글과 사용자 정보를 Optional로 조회합니다.
		Optional<FreeBoardEntity> optionalBoard = freeBoardRepository.findById(freeBoard_seq);
		Optional<UserEntity> optionalUser = userRepository.findByLoginid(loginid);

		// Optional에서 데이터를 가져올 수 있는지 확인합니다.
		if (optionalBoard.isPresent() && optionalUser.isPresent()) {
			FreeBoardEntity board = optionalBoard.get();
			UserEntity user = optionalUser.get();

			// 좋아요 여부를 확인하기 위해 좋아요 엔티티를 조회합니다.
			Optional<FreeBoardLikeEntity> optionalLike = freeBoardLikeRepository.findByUserAndFreeBoardEntity(user,
					board);

			if (optionalLike.isPresent()) {
				// 이미 좋아요를 누른 상태이면 좋아요 취소 처리합니다.
				FreeBoardLikeEntity like = optionalLike.get();
				freeBoardLikeRepository.delete(like); // 좋아요 엔티티 삭제
				board.setLikeCount(board.getLikeCount() - 1); // 게시글의 좋아요 수 감소
			} else {
				// 좋아요를 누르지 않은 상태이면 좋아요 추가 처리합니다.
				FreeBoardLikeEntity like = new FreeBoardLikeEntity();
				like.setUser(user);
				like.setFreeBoardEntity(board);
				freeBoardLikeRepository.save(like); // 좋아요 엔티티 저장
				board.setLikeCount(board.getLikeCount() + 1); // 게시글의 좋아요 수 증가
			}

			// 게시글 엔티티 저장 (좋아요 수 변경 반영)
			freeBoardRepository.save(board);

			return true; // 성공적으로 처리됨을 반환
		}

		return false; // 게시글 또는 사용자가 존재하지 않음을 반환
	}

	public boolean isLikedByUser(Long boardSeq, String loginid) {
		Optional<FreeBoardEntity> optionalBoard = freeBoardRepository.findById(boardSeq);
		Optional<UserEntity> optionalUser = userRepository.findByLoginid(loginid);
		if (optionalBoard.isPresent() && optionalUser.isPresent()) {
			FreeBoardEntity board = optionalBoard.get();
			UserEntity user = optionalUser.get();
			return freeBoardLikeRepository.findByUserAndFreeBoardEntity(user, board).isPresent();
		}
		return false;
	}

	private static final String UPLOAD_DIR = "C:/upload/"; // 새로운 파일 저장 위치

	public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 없습니다.");
        }

        // 파일명 생성
        String originalFileName = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        String filePath = UPLOAD_DIR + storedFileName;

        // 파일 저장
        File dest = new File(filePath);
        dest.getParentFile().mkdirs();
        file.transferTo(dest);

        // FileEntity 생성 및 저장
        FreeBoardFileEntity fileEntity = new FreeBoardFileEntity();
        fileEntity.setOriginalFileName(originalFileName);
        fileEntity.setStoredFileName(storedFileName);
        // assuming you have a repository for saving file entities
        freeBoardFileRepository.save(fileEntity);

        // 이미지 URL 반환
        return "/upload/" + storedFileName;
    }
	
	// 추천수 많은 3개 가져오기
    public List<FreeBoardDTO> getTop3PopularPosts() {
        List<FreeBoardEntity> popularPosts = freeBoardRepository.findTop3ByOrderByLikeCountDesc();
        return popularPosts.stream()
                           .map(FreeBoardDTO::toFreeBoardDTO)
                           .toList();
    }
	

}